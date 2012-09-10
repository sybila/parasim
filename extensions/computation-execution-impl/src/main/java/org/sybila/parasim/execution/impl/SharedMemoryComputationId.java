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
