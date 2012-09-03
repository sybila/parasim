package org.sybila.parasim.execution.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.core.ContextEvent;
import org.sybila.parasim.core.context.Context;
import org.sybila.parasim.core.extension.enrichment.api.Enrichment;
import org.sybila.parasim.execution.api.ComputationInstanceContext;
import org.sybila.parasim.execution.api.Execution;
import org.sybila.parasim.model.Mergeable;
import org.sybila.parasim.model.computation.Computation;
import org.sybila.parasim.model.computation.ComputationId;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class SharedMemoryExecution<L extends Mergeable<L>> implements Execution<L> {

    private final Collection<Execution<L>> executions;
    private volatile FutureTask<L> task;
    private final java.util.concurrent.Executor runnableExecutor;
    private final Computation computation;
    private final BlockingQueue<Future<L>> futures;

    public SharedMemoryExecution(final Collection<ComputationId> computationIds, final java.util.concurrent.Executor runnableExecutor, final Computation<L> computation, final Enrichment enrichment, final ContextEvent<ComputationInstanceContext> contextEvent, final Context parentContext, final BlockingQueue<Future<L>> futures) {
        Validate.notNull(runnableExecutor);
        Validate.notNull(computation);
        Validate.notNull(enrichment);
        Validate.notNull(contextEvent);
        Validate.notNull(parentContext);
        Validate.notNull(computationIds);
        Validate.notNull(futures);

        this.runnableExecutor = runnableExecutor;
        this.computation = computation;
        this.futures = futures;

        executions = new ArrayList<>(computationIds.size());

        for (ComputationId computationId: computationIds) {
            executions.add(new SequentialExecution<>(computationId, runnableExecutor, computation.cloneComputation(), enrichment, contextEvent, parentContext));
        }
    }

    public static <Result extends Mergeable<Result>> Execution<Result> of(final Collection<ComputationId> computationIds, final java.util.concurrent.Executor runnableExecutor, final Computation<Result> computation, final Enrichment enrichment, final ContextEvent<ComputationInstanceContext> contextEvent, final Context parentContext, final BlockingQueue<Future<Result>> futures) {
        return new SharedMemoryExecution<>(computationIds, runnableExecutor, computation, enrichment, contextEvent, parentContext, futures);
    }

    @Override
    public void abort() {
        if (task != null) {
            task.cancel(true);
        }
        for (Execution execution: executions) {
            execution.abort();
        }
    }

    @Override
    public Future<L> execute() {
        task = new FutureTask<>(new Callable<L>() {
            @Override
            public L call() throws Exception {
                try {
                    for (Execution execution: executions) {
                        while (!futures.offer(execution.execute()));
                    }
                    L result = futures.poll().get();
                    while (!futures.isEmpty()) {
                        result = result.merge(futures.poll().get());
                    }
                    return result;
                } finally {
                    computation.destroy();
                }
            }
        });
        runnableExecutor.execute(task);
        return task;
    }

    @Override
    public boolean isRunning() {
        if (task == null) {
            return false;
        }
        for (Execution execution: executions) {
            if (execution.isRunning()) {
                return true;
            }
        }
        return false;
    }
}
