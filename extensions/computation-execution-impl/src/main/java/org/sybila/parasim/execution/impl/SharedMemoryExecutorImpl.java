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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.core.ContextEvent;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.core.extension.enrichment.api.Enrichment;
import org.sybila.parasim.execution.conf.ExecutionConfiguration;
import org.sybila.parasim.execution.api.ComputationContext;
import org.sybila.parasim.execution.api.ComputationEmitter;
import org.sybila.parasim.execution.api.ComputationInstanceContext;
import org.sybila.parasim.execution.api.Execution;
import org.sybila.parasim.execution.api.SharedMemoryExecutor;
import org.sybila.parasim.execution.api.annotations.NumberOfInstances;
import org.sybila.parasim.model.Mergeable;
import org.sybila.parasim.model.computation.Computation;
import org.sybila.parasim.model.computation.ComputationId;
import org.sybila.parasim.model.computation.annotations.Before;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class SharedMemoryExecutorImpl extends AbstractExecutor implements SharedMemoryExecutor {

    private final java.util.concurrent.Executor runnableExecutor;

    public SharedMemoryExecutorImpl(ContextEvent<ComputationContext> computationContextEvent, ContextEvent<ComputationInstanceContext> computationInstanceContextEvent, Enrichment enrichment, ExecutionConfiguration configuration, java.util.concurrent.Executor runnableExecutor) {
        super(computationContextEvent, computationInstanceContextEvent, enrichment, configuration);
        Validate.notNull(runnableExecutor);
        this.runnableExecutor = runnableExecutor;
    }

    public <L extends Mergeable<L>> Execution<L> submit(Computation<L> computation) {
        int numberOfInstances = computation.getClass().getAnnotation(NumberOfInstances.class) == null ? getConfiguration().getNumberOfThreadsInSharedMemory() : computation.getClass().getAnnotation(NumberOfInstances.class).value();
        Collection<ComputationId> ids = new ArrayList<>(numberOfInstances);
        AtomicInteger maxId = new AtomicInteger(numberOfInstances-1);
        for (int i=0; i<numberOfInstances; i++) {
            ids.add(new SharedMemoryComputationId(i, maxId));
        }
        ComputationContext context = new ComputationContext();
        getComputationContextEvent().initialize(context);
        BlockingQueue<Future<L>> futures = new ArrayBlockingQueue<>(getConfiguration().getQueueSize());
        context.getStorage().add(ComputationEmitter.class, Default.class, new SharedMemoryComputationEmitter<>(runnableExecutor, getEnrichment(), getComputationInstanceContextEvent(), context, maxId, futures));
        executeMethodsByAnnotation(getEnrichment(), context, computation, Before.class);
        return new SharedMemoryExecution<>(ids,runnableExecutor,computation,getEnrichment(), getComputationContextEvent(), getComputationInstanceContextEvent(), context, futures);
    }

}