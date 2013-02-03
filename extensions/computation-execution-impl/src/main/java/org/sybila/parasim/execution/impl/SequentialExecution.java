/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
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
import org.sybila.parasim.core.annotation.Default;
import org.sybila.parasim.core.api.Binder;
import org.sybila.parasim.core.api.Context;
import org.sybila.parasim.core.api.enrichment.Enrichment;
import org.sybila.parasim.execution.api.Execution;
import org.sybila.parasim.execution.api.ExecutionResult;
import org.sybila.parasim.execution.api.annotations.ComputationInstanceScope;
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
    private final Context parentContext;
    private volatile boolean running = false;

    public SequentialExecution(final ComputationId computationId, final java.util.concurrent.Executor runnableExecutor, final Computation<L> computation, final Enrichment enrichment, final Context parentContext) {
        Validate.notNull(runnableExecutor);
        Validate.notNull(computation);
        Validate.notNull(enrichment);
        Validate.notNull(parentContext);
        Validate.notNull(computationId);
        this.runnableExecutor = runnableExecutor;
        this.computation = computation;
        this.parentContext = parentContext;
        this.task = new FutureTask<>(createCallable(computation, parentContext, enrichment, computationId));
    }

    public static <Result extends Mergeable<Result>> Execution<Result> of(final ComputationId computationId, final java.util.concurrent.Executor runnableExecutor, final Computation<Result> computation, final Enrichment enrichment, final Context parentContext) {
        return new SequentialExecution<>(computationId, runnableExecutor, computation, enrichment, parentContext);
    }

    @Override
    public void abort() {
        task.cancel(true);
        computation.destroy();
    }

    @Override
    public ExecutionResult<L> execute() {
        running = true;
        runnableExecutor.execute(task);
        return new SequentialExecutionResult<>(task);
    }

    @Override
    public boolean isRunning() {
        return running && !task.isDone() && !task.isCancelled();
    }

    protected final Callable<L> createCallable(final Computation<L> computation, final Context parentContext, final Enrichment enrichment, final ComputationId computationId) {
        return new Callable<L>() {
            @Override
            public L call() throws Exception {
                Context context = parentContext.context(ComputationInstanceScope.class);
                try {
                    ((Binder) context).bind(ComputationId.class, Default.class, computationId);
                    enrichment.enrich(computation, context);
                    return computation.call();
                } finally {
                    computation.destroy();
                    context.destroy();
                }
            }
        };
    }

    private static class SequentialExecutionResult<L extends Mergeable<L>> implements ExecutionResult<L> {

        private final Future<L> task;

        public SequentialExecutionResult(Future<L> task) {
            this.task = task;
        }

        @Override
        public Future<L> full() {
            return task;
        }

        @Override
        public Future<L> partial() {
            return task;
        }

    }

}
