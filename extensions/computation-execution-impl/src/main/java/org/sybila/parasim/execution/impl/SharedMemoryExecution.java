/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.execution.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.core.ContextEvent;
import org.sybila.parasim.core.extension.enrichment.api.Enrichment;
import org.sybila.parasim.execution.api.ComputationContext;
import org.sybila.parasim.execution.api.ComputationInstanceContext;
import org.sybila.parasim.execution.api.Execution;
import org.sybila.parasim.execution.api.ExecutionResult;
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
    private final ContextEvent<ComputationContext> parentContextEvent;
    private final ComputationContext parentContext;

    public SharedMemoryExecution(final Collection<ComputationId> computationIds, final java.util.concurrent.Executor runnableExecutor, final Computation<L> computation, final Enrichment enrichment, final ContextEvent<ComputationContext> parentContextEvent, final ContextEvent<ComputationInstanceContext> instanceContextEvent, final ComputationContext parentContext, final BlockingQueue<Future<L>> futures) {
        Validate.notNull(runnableExecutor);
        Validate.notNull(computation);
        Validate.notNull(enrichment);
        Validate.notNull(instanceContextEvent);
        Validate.notNull(parentContext);
        Validate.notNull(parentContextEvent);
        Validate.notNull(computationIds);
        Validate.notNull(futures);

        this.runnableExecutor = runnableExecutor;
        this.computation = computation;
        this.futures = futures;
        this.parentContextEvent = parentContextEvent;
        this.parentContext = parentContext;

        executions = new ArrayList<>(computationIds.size());

        for (ComputationId computationId: computationIds) {
            executions.add(new SequentialExecution<>(computationId, runnableExecutor, computation.cloneComputation(), enrichment, instanceContextEvent, parentContext));
        }
    }

    public static <Result extends Mergeable<Result>> Execution<Result> of(final Collection<ComputationId> computationIds, final java.util.concurrent.Executor runnableExecutor, final Computation<Result> computation, final Enrichment enrichment, final ContextEvent<ComputationContext> parentContextEvent, final ContextEvent<ComputationInstanceContext> instanceContextEvent, final ComputationContext parentContext, final BlockingQueue<Future<Result>> futures) {
        return new SharedMemoryExecution<>(computationIds, runnableExecutor, computation, enrichment, parentContextEvent, instanceContextEvent, parentContext, futures);
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
    public ExecutionResult<L> execute() {
        final SharedMemoryExecutionResult<L> executionResult = new SharedMemoryExecutionResult<>(runnableExecutor);
        task = new FutureTask<>(new Callable<L>() {
            @Override
            public L call() throws Exception {
                try {
                    for (Execution execution: executions) {
                        while (!futures.offer(execution.execute().full()));
                    }
                    L result = null;
                    while (!futures.isEmpty()) {
                        L current = futures.poll().get();
                        if (current != null) {
                            if (result == null) {
                                result = current;
                            } else {
                                result = result.merge(current);
                            }
                            executionResult.lastPartial = result;
                        }
                    }
                    return result;
                } finally {
                    computation.destroy();
                    parentContextEvent.finalize(parentContext);
                }
            }
        });
        executionResult.setTask(task);
        runnableExecutor.execute(task);
        return executionResult;
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

    private static class SharedMemoryExecutionResult<L extends Mergeable<L>> implements ExecutionResult<L> {

        private boolean finished;
        private L lastPartial;
        private final Executor executor;
        private Future<L> task;

        public SharedMemoryExecutionResult(Executor executor) {
            this.executor = executor;
        }

        @Override
        public Future<L> full() {
            return task;
        }

        @Override
        public Future<L> partial() {
            FutureTask<L> result = new FutureTask<>(new Callable<L>() {
                @Override
                public L call() throws Exception {
                    return poll();
                }
            });
            executor.execute(result);
            return result;
        }

        private void setTask(Future<L> task) {
            this.task = task;
        }

        private synchronized void offer(L lastPartial) {
            this.lastPartial = lastPartial;
            this.notifyAll();
        }

        private synchronized L poll() {
            while (lastPartial == null) {
                try {
                    this.wait();
                } catch (InterruptedException ignored) {
                }
            }
            return lastPartial;
        }

    }
}
