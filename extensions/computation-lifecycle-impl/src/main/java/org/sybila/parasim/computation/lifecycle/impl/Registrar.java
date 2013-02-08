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

import org.sybila.parasim.computation.lifecycle.api.ComputationContainer;
import org.sybila.parasim.computation.lifecycle.api.Executor;
import org.sybila.parasim.computation.lifecycle.api.SharedMemoryExecutor;
import org.sybila.parasim.computation.lifecycle.impl.common.DefaultComputationContainer;
import org.sybila.parasim.computation.lifecycle.impl.shared.SharedMemoryExecutorImpl;
import org.sybila.parasim.core.annotation.Provide;
import org.sybila.parasim.core.api.Context;
import org.sybila.parasim.core.api.Resolver;
import org.sybila.parasim.core.api.enrichment.Enrichment;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class Registrar {

    @Provide
    public ComputationContainer provideComputationContainer(Resolver resolver) {
        return new DefaultComputationContainer(resolver);
    }

    @Provide
    public SharedMemoryExecutor provideSharedMemoryExecutor(Enrichment enrichment, Context context) {
        return new SharedMemoryExecutorImpl(enrichment, context);
    }

    @Provide
    public Executor provideExecutor(SharedMemoryExecutor executor) {
        return executor;
    }
}
