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
