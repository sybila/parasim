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
package org.sybila.parasim.computation.density.spawn.cpu;

import java.util.ArrayList;
import java.util.Collection;
import org.sybila.parasim.model.trajectory.Distance;
import org.sybila.parasim.model.trajectory.ArrayDataBlock;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.PointTrajectory;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.trajectory.TrajectoryWithNeighborhood;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class OneTrajectorySpawner extends AbstractTrajectorySpawner {

    @Override
    protected SpawnedResult spawnTrajectories(Trajectory trajectory, Trajectory neighbor, Distance distance) {
        Collection<TrajectoryWithNeighborhood> spawnedCol = new ArrayList<>(1);
        spawnedCol.add(spawnMiddleTrajectory(trajectory, neighbor, distance));
        return new SpawnedResult(spawnedCol, null);
    }

    /**
     * Spawns a new trajectory in the middle of distance between trajectory
     * and its neighbor.
     *
     * @param trajectory
     * @param neighbor
     * @param distance
     * @return a new trajectory
     */
    protected TrajectoryWithNeighborhood spawnMiddleTrajectory(Trajectory trajectory, Trajectory neighbor, Distance distance) {
        return new PointTrajectory(new ArrayDataBlock<>(new Trajectory[] {trajectory, neighbor}), spawnMiddleInitPoint(trajectory, neighbor, distance));
    }

    /**
     * Creates a new point in the middle of distance between initial points of trajectory
     * and its neighbor
     *
     * @param trajectory
     * @param neighbor
     * @param distance
     * @return
     */
    protected Point spawnMiddleInitPoint(Trajectory trajectory, Trajectory neighbor, Distance distance) {
        float[] data = new float[trajectory.getDimension()];
        Point firstPoint = trajectory.getFirstPoint();
        Point secondPoint = neighbor.getFirstPoint();
        for (int dim = 0; dim < trajectory.getDimension(); dim++) {
            data[dim] = (firstPoint.getValue(dim) + secondPoint.getValue(dim)) / 2;
        }
        return new ArrayPoint(firstPoint.getTime(), data);
    }
}