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

import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * Represents intermediate results of cycle detection over serveral trajectories.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dra�an</a>
 */
public interface CycleDetectDataBlock<T extends Trajectory, CD extends CycleDetector> extends DataBlock<T> {

    /**
     * Returns the status of cycle detection for the given trajectory.
     *
     * @param index number from interval [0, number of trajectories)
     * @return trajectory status
     */
    CycleDetectionStatus getStatus(int index);

    /**
     * Sets the cycle detection status of trajectory with given index.
     *
     * @param index Index of trajectory who's status to modify.
     * @param status New status of given trajectory.
     */
    void setStatus(int index, CycleDetectionStatus status);

    /**
     * Returns the cycle detector used to detect cycles on trajectory with given
     * index.
     *
     * @param index Index of the detector.
     * @return Cycle detector used to detect a cycle on trajectory with given index.
     */
    CD getCycleDetector(int index);
}
