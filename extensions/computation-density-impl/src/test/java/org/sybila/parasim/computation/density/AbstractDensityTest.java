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
package org.sybila.parasim.computation.density;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sybila.parasim.computation.density.api.ArrayInitialSampling;
import org.sybila.parasim.computation.density.api.Configuration;
import org.sybila.parasim.computation.density.api.InitialSampling;
import org.sybila.parasim.computation.density.spawn.cpu.AbstractConfiguration;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.trajectory.ArrayDataBlock;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.LimitedDistance;
import org.sybila.parasim.model.trajectory.LimitedPointDistanceMetric;
import org.sybila.parasim.model.trajectory.ListDataBlock;
import org.sybila.parasim.model.trajectory.ListTrajectory;
import org.sybila.parasim.model.trajectory.MapTrajectoryNeighborhood;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.trajectory.TrajectoryNeighborhood;

public abstract class AbstractDensityTest {

    protected Configuration createConfiguration(final InitialSampling initialSampling, final OrthogonalSpace initialSpace, final TrajectoryNeighborhood trajectoryNeighborhood) {
        return new AbstractConfiguration(initialSampling, initialSpace) {
            public TrajectoryNeighborhood getNeighborhood() {
                return trajectoryNeighborhood;
            }
            public int getStartIndex(int index, int neighborIndex) {
                return 0;
            }
        };
    }

    protected InitialSampling createInitialSampling(final OrthogonalSpace space, final int numOfSpawn) {
        int[] toSpawn = new int[space.getDimension()];
        for (int dim=0; dim<space.getDimension(); dim++) {
            toSpawn[dim] = numOfSpawn;
        }
        return new ArrayInitialSampling(toSpawn);
    }

    protected OrthogonalSpace createInitialSpace(final float base, final int dimension) {
        float[] minBounds = new float[dimension];
        float[] maxBounds = new float[dimension];
        for (int dim=0; dim<dimension; dim++) {
            minBounds[dim] = 0;
            maxBounds[dim] = (float) ((dim+1) * base);
        }
        return new OrthogonalSpace(
            new ArrayPoint(0, minBounds),
            new ArrayPoint(100, maxBounds)
        );
    }

    protected LimitedPointDistanceMetric createPointDistanceMetric(final float expectedDistance, final int dimension) {
        return new LimitedPointDistanceMetric() {
            public LimitedDistance distance(float[] first, float[] second) {
                final float[] distance = new float[first.length];
                float maxDistance = 0;
                for (int dim = 0; dim < first.length; dim++) {
                    distance[dim] = Math.abs(first[dim] - second[dim]);
                    if (distance[dim] > maxDistance) {
                        maxDistance = distance[dim];
                    }
                }
                final float maxDistanceFinal = maxDistance;
                return new LimitedDistance() {

                    public boolean isValid() {
                        return value() < expectedDistance;
                    }

                    public boolean isValid(int dimensionIndex) {
                        return value(dimensionIndex) < expectedDistance;
                    }

                    public float value() {
                        return maxDistanceFinal;
                    }

                    public float value(int dimensionIndex) {
                        return distance[dimensionIndex];
                    }
                };
            }

            public LimitedDistance distance(Point first, Point second) {
                return distance(first.toArray(), second.toArray());
            }
        };
    }

    protected DataBlock<Trajectory> createDataBlock(int size, int length, int dimension, float x, float y, float z) {
        Trajectory[] trajectories = new Trajectory[size];
        for (int t = 0; t < size; t++) {
            List<Point> points = new ArrayList<Point>(length);
            for (int p = 0; p < length; p++) {
                float[] values = new float[dimension];
                for (int dim = 0; dim < dimension; dim++) {
                    values[dim] = (float) (x * t + y * p + z * dim);
                }
                points.add(new ArrayPoint((float) p, values));
            }
            trajectories[t] = new ListTrajectory(points);
        }
        return new ArrayDataBlock<Trajectory>(trajectories);
    }

    protected TrajectoryNeighborhood createNeighborhood(DataBlock<Trajectory> dataBlock) {
        final Map<Point, DataBlock<Trajectory>> neighborhood = new HashMap<Point, DataBlock<Trajectory>>();
        for (int t = 0; t < dataBlock.size(); t++) {
            List<Trajectory> trajectories = new ArrayList<Trajectory>();
            int index = 0;
            for (int i = t+1; i < dataBlock.size(); i++) {
                trajectories.add(dataBlock.getTrajectory(i));
            }
            neighborhood.put(dataBlock.getTrajectory(t).getFirstPoint(), new ListDataBlock<Trajectory>(trajectories));
        }
        return new MapTrajectoryNeighborhood(neighborhood);
    }
}
