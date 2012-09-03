
package org.sybila.parasim.execution.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import org.sybila.parasim.core.ContextEvent;
import org.sybila.parasim.core.context.Context;
import org.sybila.parasim.core.extension.enrichment.api.Enrichment;
import org.sybila.parasim.execution.api.ComputationEmitter;
import org.sybila.parasim.execution.api.ComputationInstanceContext;
import org.sybila.parasim.model.Mergeable;
import org.sybila.parasim.model.computation.Computation;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class SharedMemoryComputationEmitter<L extends Mergeable<L>> implements ComputationEmitter<L> {

    private final AtomicInteger maxId;
    private final Context parentContext;
    private final java.util.concurrent.Executor runnableExecutor;
    private final Enrichment enrichment;
    private final ContextEvent<ComputationInstanceContext> computationInstanceContextEvent;
    private final BlockingQueue<Future<L>> futures;

    public SharedMemoryComputationEmitter(java.util.concurrent.Executor runnableExecutor, Enrichment enrichment, ContextEvent<ComputationInstanceContext> computationInstanceContextEvent, Context parentContext, AtomicInteger maxId, BlockingQueue<Future<L>> futures) {
        this.runnableExecutor = runnableExecutor;
        this.enrichment = enrichment;
        this.computationInstanceContextEvent = computationInstanceContextEvent;
        this.maxId = maxId;
        this.parentContext = parentContext;
        this.futures = futures;
    }

    @Override
    public void emit(final Computation<L> computation) {
        futures.offer(SequentialExecution.of(
                new SharedMemoryComputationId(maxId.incrementAndGet(), maxId),
                runnableExecutor,
                (Computation) computation,
                enrichment,
                computationInstanceContextEvent,
                parentContext).execute());
    }
}
