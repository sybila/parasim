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
