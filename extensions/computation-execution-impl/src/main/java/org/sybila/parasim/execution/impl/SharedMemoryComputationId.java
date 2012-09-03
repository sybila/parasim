package org.sybila.parasim.execution.impl;

import java.util.concurrent.atomic.AtomicInteger;
import org.sybila.parasim.model.computation.ComputationId;

/**
 *
 * @author jpapouse
 */
public class SharedMemoryComputationId implements ComputationId {

    private final int currentId;
    private final AtomicInteger maxId;

    public SharedMemoryComputationId(int currentId, AtomicInteger maxId) {
        this.currentId = currentId;
        this.maxId = maxId;
    }

    @Override
    public int currentId() {
        return currentId;
    }

    @Override
    public int maxId() {
        return maxId.get();
    }
}
