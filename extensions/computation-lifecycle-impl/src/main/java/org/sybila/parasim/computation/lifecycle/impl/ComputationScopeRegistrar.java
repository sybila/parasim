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
package org.sybila.parasim.computation.lifecycle.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.sybila.parasim.computation.lifecycle.api.annotations.ComputationScope;
import org.sybila.parasim.computation.lifecycle.impl.common.ComputationLifecycleConfiguration;
import org.sybila.parasim.core.annotation.Observes;
import org.sybila.parasim.core.annotation.Provide;
import org.sybila.parasim.core.event.After;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
@ComputationScope
public class ComputationScopeRegistrar {

    private ExecutorService executorService;

    public void destroyExecutorService(@Observes After event) throws InterruptedException {
        if (event.getLoad().equals(ComputationScope.class) && executorService != null) {
            executorService.shutdownNow();
        }
    }

    @Provide
    public ExecutorService provideExecutorService(ComputationLifecycleConfiguration configuration) {
        executorService =  Executors.newFixedThreadPool(configuration.getNumberOfThreads());
        return executorService;
    }

}