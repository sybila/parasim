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
package org.sybila.parasim.execution;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.sybila.parasim.core.ContextEvent;
import org.sybila.parasim.core.Instance;
import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.annotations.Observes;
import org.sybila.parasim.core.annotations.Provide;
import org.sybila.parasim.core.event.ManagerStopping;
import org.sybila.parasim.core.extension.cdi.api.ServiceFactory;
import org.sybila.parasim.execution.api.ComputationContext;
import org.sybila.parasim.execution.api.Executor;
import org.sybila.parasim.execution.api.SequentialExecutor;
import org.sybila.parasim.execution.api.SharedMemoryExecutor;
import org.sybila.parasim.execution.impl.SequentialExecutorImpl;
import org.sybila.parasim.execution.impl.SharedMemoryExecutorImpl;
import org.sybila.parasim.execution.conf.ExecutionConfiguration;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ExecutorRegistrar {

    @Inject
    private ContextEvent<ComputationContext> contextEvent;
    @Inject
    private Instance<java.util.concurrent.Executor> executor;

    @Provide
    public java.util.concurrent.Executor provideRunnableExecutor(ExecutionConfiguration configuration) {
        return new ThreadPoolExecutor(
                configuration.getCoreThreadPoolSize(),
                configuration.getMaxThreadPoolSize(),
                configuration.getKeepThreadAliveTimeInSeconds(),
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(configuration.getQueueSize()));
    }

    public void destroyRunnableExecutor(@Observes ManagerStopping event) {
        if (executor.get() != null && executor.get() instanceof ThreadPoolExecutor) {
            ((ThreadPoolExecutor) executor.get()).shutdown();
        }
    }

    @Provide
    public SequentialExecutor provideSequentialExecutor(java.util.concurrent.Executor runnableExecutor, ServiceFactory serviceFactory, ExecutionConfiguration configuration) {
        return new SequentialExecutorImpl(contextEvent, serviceFactory, runnableExecutor, configuration);
    }

    @Provide
    public SharedMemoryExecutor provideSharedMemoryExecutor(java.util.concurrent.Executor runnableExecutor, ServiceFactory serviceFactory, ExecutionConfiguration configuration) {
        return new SharedMemoryExecutorImpl(contextEvent, serviceFactory, runnableExecutor, configuration);
    }

    @Provide
    public Executor provideExecutor(ExecutionConfiguration configuration, Manager manager) throws ClassNotFoundException {
        return (Executor) manager.resolve(Class.forName(configuration.getDefaultExecutorClass()), Default.class, manager.getRootContext());
    }
}
