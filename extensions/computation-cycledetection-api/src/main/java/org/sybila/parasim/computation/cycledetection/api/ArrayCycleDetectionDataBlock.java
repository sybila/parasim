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
package org.sybila.parasim.computation.cycledetection.api;

import java.util.Iterator;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * Represents a cycle detection data block of trajectories being cycle detected.
 * All cycle detectors and computation statuses are held in arrays.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public class ArrayCycleDetectionDataBlock<T extends Trajectory, CD extends CycleDetector> implements CycleDetectDataBlock<T, CD> {

    private DataBlock<T> dataBlock;
    private CD[] cycleDetectors;
    private CycleDetectionStatus[] statuses;

    public ArrayCycleDetectionDataBlock(DataBlock<T> dataBlock,
            CD[] cycleDetectors, CycleDetectionStatus[] statuses) {
        if (dataBlock == null) {
            throw new IllegalArgumentException("The parameter dataBlock is null.");
        }
        if (cycleDetectors == null) {
            throw new IllegalArgumentException("The parameter cycleDetectors is null.");
        }
        if (statuses == null) {
            throw new IllegalArgumentException("The parameter statuses is null.");
        }
        if (dataBlock.size() != cycleDetectors.length) {
            throw new IllegalArgumentException("The number of trajectories does not match number of cycle detectors.");
        }
        if (dataBlock.size() != statuses.length) {
            throw new IllegalArgumentException("The number of trajectories in data block doesn't match the number of statuses.");
        }
        this.dataBlock = dataBlock;
        this.cycleDetectors = cycleDetectors;
        this.statuses = statuses;
    }

    @Override
    public CD getCycleDetector(int index) {
        if (index < 0 || index > cycleDetectors.length) {
            throw new IllegalArgumentException("Index must be in range [0, " + cycleDetectors.length + ").");
        }
        return cycleDetectors[index];
    }

    @Override
    public CycleDetectionStatus getStatus(int index) {
        if (index < 0 || index > statuses.length) {
            throw new IllegalArgumentException("Index must be in range [0, " + statuses.length + ")");
        }
        return statuses[index];
    }

    @Override
    public T getTrajectory(int index) {
        if (index < 0 || index > dataBlock.size()) {
            throw new IllegalArgumentException("Index must be in range [0, " + dataBlock.size() + ")");
        }
        return dataBlock.getTrajectory(index);
    }

    @Override
    public Iterator<T> iterator() {
        return dataBlock.iterator();
    }

    @Override
    public int size() {
        return dataBlock.size();
    }

    @Override
    public void setStatus(int index, CycleDetectionStatus status) {
        if (index < 0 || index >= statuses.length) {
            throw new IllegalArgumentException("Index must be in range [0, " + statuses.length + ")");
        }
        statuses[index] = status;
    }
}
