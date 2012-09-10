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

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.core.ContextEvent;
import org.sybila.parasim.core.annotations.Default;
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
public class SequentialExecution<L extends Mergeable<L>> implements Execution<L> {

    private final FutureTask<L> task;
    private final java.util.concurrent.Executor runnableExecutor;
    private final Computation computation;
    private volatile boolean running = false;

    public SequentialExecution(final ComputationId computationId, final java.util.concurrent.Executor runnableExecutor, final Computation<L> computation, final Enrichment enrichment, final ContextEvent<ComputationInstanceContext> contextEvent, final Context parentContext) {
        Validate.notNull(runnableExecutor);
        Validate.notNull(computation);
        Validate.notNull(enrichment);
        Validate.notNull(contextEvent);
        Validate.notNull(computationId);
        this.runnableExecutor = runnableExecutor;
        this.computation = computation;
        this.task = new FutureTask<>(createCallable(computation, contextEvent, parentContext, enrichment, computationId));
    }

    public static <Result extends Mergeable<Result>> Execution<Result> of(final ComputationId computationId, final java.util.concurrent.Executor runnableExecutor, final Computation<Result> computation, final Enrichment enrichment, final ContextEvent<ComputationInstanceContext> contextEvent, final Context parentContext) {
        return new SequentialExecution<>(computationId, runnableExecutor, computation, enrichment, contextEvent, parentContext);
    }

    @Override
    public void abort() {
        task.cancel(true);
        computation.destroy();
    }

    @Override
    public Future<L> execute() {
        running = true;
        runnableExecutor.execute(task);
        return task;
    }

    @Override
    public boolean isRunning() {
        return running && !task.isDone() && !task.isCancelled();
    }

    protected final Callable<L> createCallable(final Computation<L> computation, final ContextEvent<ComputationInstanceContext> contextEvent, final Context parentContext, final Enrichment enrichment, final ComputationId computationId) {
        return new Callable<L>() {
            @Override
            public L call() throws Exception {
                ComputationInstanceContext context = new ComputationInstanceContext();
                try {
                    contextEvent.initialize(context);
                    context.setParent(parentContext);
                    context.getStorage().add(ComputationId.class, Default.class, computationId);
                    enrichment.enrich(computation, context);
                    return computation.call();
                } finally {
                    computation.destroy();
                    contextEvent.finalize(context);
                }
            }
        };
    }

}
