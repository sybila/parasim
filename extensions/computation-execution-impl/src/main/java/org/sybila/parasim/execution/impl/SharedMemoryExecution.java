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
import org.sybila.parasim.core.extension.cdi.api.ServiceFactory;
import org.sybila.parasim.execution.api.ComputationContext;
import org.sybila.parasim.execution.api.Execution;
import org.sybila.parasim.model.Mergeable;
import org.sybila.parasim.model.computation.Computation;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class SharedMemoryExecution<Result extends Mergeable<Result>> implements Execution {

    private final Execution[] executions;
    private volatile FutureTask<Result> task;
    private final java.util.concurrent.Executor runnableExecutor;
    private final Computation computation;

    private SharedMemoryExecution(final java.util.concurrent.Executor runnableExecutor, final Computation<Result> computation, final ServiceFactory serviceFactory, final ContextEvent<ComputationContext> contextEvent, final int threadIdFrom, final int threadIdTo, final int threadMaxId) {
        Validate.notNull(runnableExecutor);
        Validate.notNull(computation);
        Validate.notNull(serviceFactory);
        Validate.notNull(contextEvent);
        this.runnableExecutor = runnableExecutor;
        if (threadMaxId <= 0) {
            throw new IllegalArgumentException("The paramater [threadMaxId] has to be a positive number.");
        }
        executions = new Execution[threadIdTo - threadIdFrom + 1];
        this.computation = computation;
        for (int threadId=threadIdFrom; threadId<=threadIdTo; threadId++) {
            executions[threadId-threadIdFrom] = SequentialExecution.of(runnableExecutor, computation.cloneComputation(), serviceFactory, contextEvent, threadId, threadMaxId);
        }
    }

    public static <R extends Mergeable<R>> Execution<R> of(final java.util.concurrent.Executor runnableExecutor, final Computation<R> computation, final ServiceFactory serviceFactory, final ContextEvent<ComputationContext> contextEvent, final int threadIdFrom, final int threadIdTo, final int threadMaxId) {
        return new SharedMemoryExecution<R>(runnableExecutor, computation, serviceFactory, contextEvent, threadIdFrom, threadIdTo, threadMaxId);
    }

    public void abort() {
        if (task != null) {
            task.cancel(true);
        }
        for (Execution execution: executions) {
            execution.abort();
        }
    }

    public Future execute() {
        task = new FutureTask<Result>(new Callable<Result>() {
            public Result call() throws Exception {
                try {
                    Future[] futures = new Future[executions.length];
                    for (int i=0; i<executions.length; i++) {
                        futures[i] = executions[i].execute();
                    }
                    Result result = (Result) futures[0].get();
                    for (int i=1; i<executions.length; i++) {
                        result = result.merge((Result) futures[i].get());
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