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

import java.util.concurrent.Executor;
import org.sybila.parasim.core.ContextEvent;
import org.sybila.parasim.core.extension.cdi.api.ServiceFactory;
import org.sybila.parasim.execution.conf.ExecutionConfiguration;
import org.sybila.parasim.execution.api.ComputationContext;
import org.sybila.parasim.execution.api.Execution;
import org.sybila.parasim.execution.api.SequentialExecutor;
import org.sybila.parasim.model.computation.Computation;
import org.sybila.parasim.model.Mergeable;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class SequentialExecutorImpl extends AbstractExecutor implements SequentialExecutor {

    public SequentialExecutorImpl(ContextEvent<ComputationContext> contextEvent, ServiceFactory serviceFactory, Executor runnableExecutor, ExecutionConfiguration configuration) {
        super(contextEvent, serviceFactory, runnableExecutor, configuration);
    }

    public <L extends Mergeable<L>> Execution<L> execute(Computation<L> computation) {
        return SequentialExecution.of(getRunnableExecutor(), computation, getServiceFactory(), getContextEvent(), 0, 0);
    }
}