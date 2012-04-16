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
package org.sybila.parasim.computation.lifecycle.impl;

import java.util.Map;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.computation.lifecycle.api.AbortionException;
import org.sybila.parasim.model.computation.Computation;
import org.sybila.parasim.computation.lifecycle.api.ComputationContainer;
import org.sybila.parasim.computation.lifecycle.api.annotations.RunWith;
import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.execution.api.Execution;
import org.sybila.parasim.execution.api.Executor;
import org.sybila.parasim.model.Mergeable;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class DefaultComputationContainer implements ComputationContainer {

    private Manager manager;
    private Map<Computation, Execution> executions;

    public DefaultComputationContainer(Manager manager) {
        Validate.notNull(manager);
        this.manager = manager;
    }

    @Override
    public void abort(Computation<?> computation) throws AbortionException {
        Execution execution = executions.get(computation);
        if (execution != null) {
            execution.abort();
        }
    }

    @Override
    public <Result extends Mergeable<Result>> Future<Result> compute(Computation<Result> computation) {
        Validate.notNull(computation);
        if (executions.containsKey(computation)) {
            throw new IllegalArgumentException("The computation can't be computed, because it's already being computed by this container.");
        }
        if (computation.isDestroyed()) {
            throw new IllegalArgumentException("The computation is already destroyed.");
        }
        RunWith runWith = computation.getClass().getAnnotation(RunWith.class);
        if (runWith == null) {
            try {
                runWith = computation.getClass().getMethod("compute").getAnnotation(RunWith.class);
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(DefaultComputationContainer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(DefaultComputationContainer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Class<? extends Executor> executorClass = runWith == null ? Executor.class : runWith.executor();
        Executor executor = manager.resolve(executorClass, Default.class, manager.getRootContext());
        Execution execution = executor.execute(computation);
        executions.put(computation, execution);
        return execution.execute();
    }

    @Override
    public void destroy(Computation<?> computation) {
        Validate.notNull(computation);
        if (computation.isDestroyed()) {
            throw new IllegalArgumentException("The computation is already destroyed.");
        }
        Execution execution = executions.get(computation);
        if (execution != null) {
            if (execution.isRunning()) {
                throw new IllegalStateException("Can't destroy the computation, because it's running.");
            }
            executions.remove(computation);
        }
        computation.destroy();
    }
}