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
import java.util.List;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class CycleDetectedDataBlockWrapper<T extends Trajectory> implements CycleDetectedDataBlock<T> {

    private final DataBlock<T> trajectories;
    private final List<CycleDetector> cycleDetectors;

    public CycleDetectedDataBlockWrapper(DataBlock<T> trajectories, List<CycleDetector> cycleDetectors) {
        this.trajectories = trajectories;
        this.cycleDetectors = cycleDetectors;
    }

    public CycleDetectedDataBlockWrapper(DataBlock<T> trajectories) {
        this.trajectories = trajectories;
        this.cycleDetectors = null;
    }

    @Override
    public CycleDetector getCycleDetector(int index) {
        return cycleDetectors == null ? CycleDetector.CYCLE_IS_NOT_DETECTED : cycleDetectors.get(index);
    }

    @Override
    public T getTrajectory(int index) {
        return trajectories.getTrajectory(index);
    }

    @Override
    public int size() {
        return trajectories.size();
    }

    @Override
    public Iterator<T> iterator() {
        return trajectories.iterator();
    }

}
