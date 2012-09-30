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
                parentContext).execute().full());
    }
}
