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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.core.ContextEvent;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.core.annotations.Qualifier;
import org.sybila.parasim.core.context.Context;
import org.sybila.parasim.core.extension.cdi.api.ServiceFactory;
import org.sybila.parasim.execution.api.ComputationContext;
import org.sybila.parasim.model.computation.annotations.Before;
import org.sybila.parasim.execution.api.Execution;
import org.sybila.parasim.model.computation.annotations.After;
import org.sybila.parasim.model.computation.Computation;
import org.sybila.parasim.model.Mergeable;
import org.sybila.parasim.model.computation.ThreadId;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class SequentialExecution<Result extends Mergeable<Result>> implements Execution<Result> {

    private final FutureTask<Result> task;
    private final java.util.concurrent.Executor runnableExecutor;
    private final Computation computation;
    private volatile boolean running = false;

    private SequentialExecution(final java.util.concurrent.Executor runnableExecutor, final Computation<Result> computation, final ServiceFactory serviceFactory, final ContextEvent<ComputationContext> contextEvent, final int threadId, final int threadMaxId) {
        Validate.notNull(runnableExecutor);
        Validate.notNull(computation);
        Validate.notNull(serviceFactory);
        Validate.notNull(contextEvent);
        if (threadMaxId < 0) {
            throw new IllegalArgumentException("The paramater [threadMaxId] has to be a non negative number.");
        }
        this.runnableExecutor = runnableExecutor;
        this.computation = computation;
        this.task = new FutureTask<Result>(new Callable<Result>() {

            public Result call() throws Exception {
                ComputationContext context = new ComputationContext();
                contextEvent.initialize(context);
                try {
                    context.getStorage().add(
                        ThreadId.class,
                        Default.class,
                        new ThreadId() {
                            public int currentId() {
                                return threadId;
                            }
                            public int maxId() {
                                return threadMaxId;
                            }
                        }
                    );
                    serviceFactory.provideFieldsAndMethods(computation, context);
                    serviceFactory.injectFields(computation, context);
                    try {
                        executeMethodsByAnnotation(serviceFactory, context, computation, Before.class);
                        Result result = computation.call();
                        return result;
                    } finally {
                        executeMethodsByAnnotation(serviceFactory, context, computation, After.class);
                    }
                } finally {
                    computation.destroy();
                    contextEvent.finalize(context);
                }
            }
        });
    }

    public static <R extends Mergeable<R>> Execution<R> of(final java.util.concurrent.Executor runnableExecutor, final Computation<R> computation, final ServiceFactory serviceFactory, final ContextEvent<ComputationContext> contextEvent, final int threadId, final int threadMaxId) {
        return new SequentialExecution<R>(runnableExecutor, computation, serviceFactory, contextEvent, threadId, threadMaxId);
    }

    public void abort() {
        task.cancel(true);
        computation.destroy();
    }

    public Future<Result> execute() {
        running = true;
        runnableExecutor.execute(task);
        return task;
    }

    public boolean isRunning() {
        return running && !task.isDone() && !task.isCancelled();
    }

    protected void executeMethodsByAnnotation(final ServiceFactory serviceFactory, final Context context, final Object target, final Class<? extends Annotation> annotation) {
        for (Method method : target.getClass().getDeclaredMethods()) {
            if (method.getAnnotation(annotation) != null) {
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                Object[] params = new Object[method.getParameterTypes().length];
                for (int i = 0; i < method.getParameterTypes().length; i++) {
                    Class<? extends Annotation> qualifier = getParameterQualifier(method, i);
                    if (qualifier != null) {
                        params[i] = serviceFactory.getService(method.getParameterTypes()[i], context, qualifier);
                    } else {
                        params[i] = serviceFactory.getService(method.getParameterTypes()[i], context);
                    }
                    if (params[i] == null) {
                        throw new IllegalStateException("There is no implementation instance for " + method.getParameterTypes()[i] + " which can be passed as an argument in " + target.getClass() + "#" + method.getName() + "()");
                    }
                    try {
                        method.invoke(target, params);
                    } catch (Exception ex) {
                        throw new IllegalStateException("Can't invoke " + target.getClass() + "#" + method.getName() + "()");
                    }
                }
            }
        }
    }

    protected Class<? extends Annotation> getParameterQualifier(Method method, int paramIndex) {
        for (int i = 0; i < method.getParameterAnnotations()[paramIndex].length; i++) {
            if (method.getParameterAnnotations()[paramIndex][i].getClass().getAnnotation(Qualifier.class) != null) {
                return method.getParameterAnnotations()[paramIndex][i].getClass();
            }
        }
        return null;
    }

}