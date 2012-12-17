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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.sybila.parasim.computation.density.api.Configuration;
import org.sybila.parasim.computation.density.spawn.cpu.AbstractConfiguration;
import org.sybila.parasim.model.math.Constant;
import org.sybila.parasim.model.ode.OdeSystemVariable;
import org.sybila.parasim.model.ode.SimpleOdeSystem;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.space.OrthogonalSpaceImpl;
import org.sybila.parasim.model.trajectory.ArrayDataBlock;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.LimitedDistance;
import org.sybila.parasim.model.trajectory.LimitedPointDistanceMetric;
import org.sybila.parasim.model.trajectory.ListDataBlock;
import org.sybila.parasim.model.trajectory.ListTrajectory;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.trajectory.TrajectoryWithNeighborhood;
import org.sybila.parasim.model.trajectory.TrajectoryWithNeighborhoodWrapper;

public abstract class AbstractDensityTest {

    protected Configuration createConfiguration(final OrthogonalSpace initialSpace) {
        return new AbstractConfiguration(initialSpace) {
            @Override
            public int getStartIndex(int index, int neighborIndex) {
                return 0;
            }
        };
    }

    protected OrthogonalSpace createInitialSpace(final float base, final int dimension) {
        float[] minBounds = new float[dimension];
        float[] maxBounds = new float[dimension];
        Collection<OdeSystemVariable> vars = new ArrayList<>();
        for (int dim=0; dim<dimension; dim++) {
            minBounds[dim] = 0;
            maxBounds[dim] = (float) ((dim+1) * base);
            vars.add(new OdeSystemVariable("x" + dim, dim, new Constant(dim)));
        }
        return new OrthogonalSpaceImpl(
            new ArrayPoint(0, minBounds),
            new ArrayPoint(100, maxBounds),
            new SimpleOdeSystem(vars, Collections.EMPTY_LIST, Collections.EMPTY_LIST)
        );
    }

    protected LimitedPointDistanceMetric createPointDistanceMetric(final float expectedDistance, final int dimension) {
        return new LimitedPointDistanceMetric() {
            @Override
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

                    @Override
                    public boolean isValid() {
                        return value() < expectedDistance;
                    }

                    public boolean isValid(int dimensionIndex) {
                        return value(dimensionIndex) < expectedDistance;
                    }

                    @Override
                    public float value() {
                        return maxDistanceFinal;
                    }

                    public float value(int dimensionIndex) {
                        return distance[dimensionIndex];
                    }
                };
            }

            @Override
            public LimitedDistance distance(Point first, Point second) {
                return distance(first.toArray(), second.toArray());
            }
        };
    }

    protected DataBlock<TrajectoryWithNeighborhood> createDataBlock(int size, int length, int dimension, float x, float y, float z) {
        // TODO: create also neighbors
        Trajectory[] trajectories = new Trajectory[size];
        for (int t = 0; t < size; t++) {
            List<Point> points = new ArrayList<>(length);
            for (int p = 0; p < length; p++) {
                float[] values = new float[dimension];
                for (int dim = 0; dim < dimension; dim++) {
                    values[dim] = (float) (x * t + y * p + z * dim);
                }
                points.add(new ArrayPoint((float) p, values));
            }
            trajectories[t] = new ListTrajectory(points);
        }
        TrajectoryWithNeighborhood[] result = new TrajectoryWithNeighborhood[size];
        DataBlock<Trajectory> dataBlock =  new ArrayDataBlock<>(trajectories);
        for (int t = 0; t < dataBlock.size(); t++) {
            List<Trajectory> neighbors = new ArrayList<>();
            int index = 0;
            for (int i = t+1; i < dataBlock.size(); i++) {
                neighbors.add(dataBlock.getTrajectory(i));
            }
            result[t] = TrajectoryWithNeighborhoodWrapper.createAndUpdateReference(dataBlock.getTrajectory(t), new ListDataBlock<>(neighbors));
        }
        return new ArrayDataBlock<>(result);
    }
}
