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

import org.sybila.parasim.core.ContextEvent;
import org.sybila.parasim.core.extension.cdi.api.ServiceFactory;
import org.sybila.parasim.execution.conf.ExecutionConfiguration;
import org.sybila.parasim.execution.api.ComputationContext;
import org.sybila.parasim.execution.api.Executor;

/**
 *
 * @author jpapouse
 */
public abstract class AbstractExecutor implements Executor {

    private final ContextEvent<ComputationContext> contextEvent;
    private final ServiceFactory serviceFactory;
    private final java.util.concurrent.Executor runnableExecutor;
    private final ExecutionConfiguration configuration;

    public AbstractExecutor(final ContextEvent<ComputationContext> contextEvent, final ServiceFactory serviceFactory, final java.util.concurrent.Executor runnableExecutor, final ExecutionConfiguration configuration) {
        this.contextEvent = contextEvent;
        this.serviceFactory = serviceFactory;
        this.runnableExecutor = runnableExecutor;
        this.configuration = configuration;
    }

    protected ExecutionConfiguration getConfiguration() {
        return configuration;
    }

    protected ContextEvent<ComputationContext> getContextEvent() {
        return contextEvent;
    }

    protected java.util.concurrent.Executor getRunnableExecutor() {
        return runnableExecutor;
    }

    protected ServiceFactory getServiceFactory() {
        return serviceFactory;
    }
}
