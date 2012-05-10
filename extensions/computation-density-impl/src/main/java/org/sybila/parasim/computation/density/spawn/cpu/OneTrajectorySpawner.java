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
import java.util.HashMap;
import java.util.Map;
import org.sybila.parasim.model.trajectory.Distance;
import org.sybila.parasim.model.trajectory.ArrayDataBlock;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.PointTrajectory;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class OneTrajectorySpawner extends AbstractTrajectorySpawner {

    protected SpawnedResult spawnTrajectories(Trajectory trajectory, Trajectory neighbor, Distance distance) {
        Map<Point, DataBlock<Trajectory>> neighborhood = new HashMap<Point, DataBlock<Trajectory>>();
        Trajectory spawned = spawnMiddleTrajectory(trajectory, neighbor, distance);
        neighborhood.put(
                spawned.getFirstPoint(),
                new ArrayDataBlock<Trajectory>(
                new Trajectory[]{
                    trajectory,
                    neighbor
                }));
        Collection<Trajectory> spawnedCol = new ArrayList<Trajectory>(1);
        spawnedCol.add(spawned);
        return new SpawnedResult(neighborhood, spawnedCol, null);
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
    protected Trajectory spawnMiddleTrajectory(Trajectory trajectory, Trajectory neighbor, Distance distance) {
        return new PointTrajectory(spawnMiddleInitPoint(trajectory, neighbor, distance));
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