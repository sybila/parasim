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
package org.sybila.parasim.computation.density.distancecheck.api;

import java.util.Iterator;
import java.util.List;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.LimitedDistance;
import org.sybila.parasim.model.trajectory.TrajectoryWithNeighborhood;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ListDistanceCheckedDataBlock implements DistanceCheckedDataBlock {

    private DataBlock<TrajectoryWithNeighborhood> dataBlock;
    private List<List<LimitedDistance>> distances;
    private List<List<Integer>> neighborCheckedPositions;
    private List<List<Integer>> trajectoryCheckedPositions;

    public ListDistanceCheckedDataBlock(DataBlock<TrajectoryWithNeighborhood> dataBlock, List<List<LimitedDistance>> distances, List<List<Integer>> trajectoryCheckedPositions, List<List<Integer>> neighborCheckedPositions) {
        if (dataBlock == null) {
            throw new IllegalArgumentException("The parameter dataBlock is null.");
        }
        if (distances == null) {
            throw new IllegalArgumentException("The parameter distances is null.");
        }
        if (distances.size() != dataBlock.size()) {
            throw new IllegalArgumentException("The number of distances doesn't correspond to the number of trajectories.");
        }
        if (trajectoryCheckedPositions == null) {
            throw new IllegalArgumentException("The parameter trajectoryCheckedPositions is null.");
        }
        if (trajectoryCheckedPositions.size() != dataBlock.size()) {
            throw new IllegalArgumentException("The number of trajectory checked positions doesn't correspond to the number of trajectories.");
        }
        if (neighborCheckedPositions == null) {
            throw new IllegalArgumentException("The parameter neighborCheckedPositions is null.");
        }
        if (neighborCheckedPositions.size() != dataBlock.size()) {
            throw new IllegalArgumentException("The number of neighbor checked positions doesn't correspond to the number of trajectories.");
        }
        this.dataBlock = dataBlock;
        this.distances = distances;
        this.trajectoryCheckedPositions = trajectoryCheckedPositions;
        this.neighborCheckedPositions = neighborCheckedPositions;
    }

    @Override
    public LimitedDistance getDistance(int index, int neighborIndex) {
        if (index < 0 || index >= distances.size()) {
            throw new IndexOutOfBoundsException("The trajectory index is out range [0, " + (distances.size() - 1) + "].");
        }
        List<LimitedDistance> found = distances.get(index);
        if (neighborIndex < 0 || neighborIndex >= found.size()) {
            throw new IndexOutOfBoundsException("The neighbor index is out range [0, " + (found.size() - 1) + "].");
        }
        return found.get(neighborIndex);
    }

    @Override
    public TrajectoryWithNeighborhood getTrajectory(int index) {
        return dataBlock.getTrajectory(index);
    }

    @Override
    public int size() {
        return dataBlock.size();
    }

    @Override
    public Iterator<TrajectoryWithNeighborhood> iterator() {
        return dataBlock.iterator();
    }

    @Override
    public int getTrajectoryCheckedPosition(int index, int neighborIndex) {
        return trajectoryCheckedPositions.get(index).get(neighborIndex);
    }

    @Override
    public int getNeighborCheckedPosition(int index, int neighborIndex) {
        return neighborCheckedPositions.get(index).get(neighborIndex);
    }
}
