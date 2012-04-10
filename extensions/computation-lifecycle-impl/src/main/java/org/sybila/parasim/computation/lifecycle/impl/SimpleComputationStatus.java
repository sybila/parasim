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

import org.sybila.parasim.computation.lifecycle.api.ComputationStatus;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class SimpleComputationStatus implements ComputationStatus {

    private volatile boolean finalized;
    private volatile boolean finished;
    private volatile boolean initialized;
    private volatile boolean started;
    private long lastConsumedTime = 0;
    private long lastStartRunningTime = -1;
    private long totalConsumedTime = 0;
    private volatile int numberOfRunning;

    @Override
    public boolean isFinalized() {
        return finalized;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public boolean isRunning() {
        return numberOfRunning > 0;
    }

    @Override
    public long getLastConsumedTime() {
        if (isRunning()) {
            throw new IllegalStateException("The computation is running.");
        }
        return lastConsumedTime;
    }

    @Override
    public long getTotalConsumedTime() {
        return totalConsumedTime;
    }

    @Override
    public void setFinalized() {
        finalized = true;
    }

    @Override
    public void setFinished() {
        finished = true;
    }

    @Override
    public void setInitialized() {
        initialized = true;
    }

    @Override
    public void startRunning() {
        numberOfRunning++;
        started = true;
        synchronized (this) {
            if (lastStartRunningTime == -1) {
                lastStartRunningTime = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void stopRunning() {
        synchronized (this) {
            numberOfRunning--;
            if (numberOfRunning == 0) {
                lastConsumedTime = System.currentTimeMillis() - lastStartRunningTime;
                totalConsumedTime += lastConsumedTime;
                lastStartRunningTime = -1;

            }
        }
    }
}
