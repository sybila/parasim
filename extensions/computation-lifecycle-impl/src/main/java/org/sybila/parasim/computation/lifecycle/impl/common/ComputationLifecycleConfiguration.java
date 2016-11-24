/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.computation.lifecycle.impl.common;

import java.net.URI;
import org.sybila.parasim.computation.lifecycle.api.SharedMemoryExecutor;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ComputationLifecycleConfiguration {

    private int numberOfThreads = Runtime.getRuntime().availableProcessors();
    private long nodeThreshold = Runtime.getRuntime().availableProcessors() + Runtime.getRuntime().availableProcessors() / 2;
    private float balancerMultiplier = 1.5f;
    private int balancerBusyBound = 1;
    private int balancerIdleBound = 1;
    private URI[] nodes;
    private String defaultExecutor = SharedMemoryExecutor.class.getName();

    public float getBalancerMultiplier() {
        return balancerMultiplier;
    }

    public int getBalancerBusyBound() {
        return balancerBusyBound;
    }

    public int getBalancerIdleBound() {
        return balancerIdleBound;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public long getNodeThreshold() {
        return nodeThreshold;
    }

    public URI[] getNodes() {
        return nodes == null ? new URI[0] : nodes;
    }

    public Class<?> getDefaultExecutor() throws ClassNotFoundException {
        return Class.forName(defaultExecutor);
    }

}
