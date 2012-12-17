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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.sybila.parasim.computation.density.api.Configuration;
import org.sybila.parasim.computation.density.distancecheck.api.DistanceCheckedDataBlock;
import org.sybila.parasim.computation.density.spawn.api.SpawnedDataBlock;
import org.sybila.parasim.computation.density.spawn.api.SpawnedDataBlockWrapper;
import org.sybila.parasim.computation.density.spawn.api.TrajectorySpawner;
import org.sybila.parasim.computation.density.spawn.cpu.FractionPoint.Fraction;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.trajectory.LimitedDistance;
import org.sybila.parasim.model.trajectory.ListDataBlock;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.PointTrajectory;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.trajectory.TrajectoryWithNeighborhood;
import org.sybila.parasim.model.trajectory.TrajectoryWithNeighborhoodWrapper;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class FractionTrajectorySpawner implements TrajectorySpawner {

    private final Map<FractionPoint, Trajectory> alreadySpawnedCollisionTrajectories = new WeakHashMap<>();
    private final Map<FractionPoint, Trajectory> alreadySpawnedPrimaryTrajectories = new WeakHashMap<>();

    @Override
    public SpawnedDataBlock spawn(Configuration configuration, DistanceCheckedDataBlock trajectories) {
        // note spawned trajectories
        List<TrajectoryWithNeighborhood> newTrajectories = new ArrayList<>();
        // note secondary trajectories
        Set<Trajectory> newSecondaryTrajectories = new HashSet<>();
        // iterate through all pairs of trajectory and neighbor with invalid distance
        for (int i = 0; i < trajectories.size(); i++) {
            TrajectoryWithNeighborhood trajectory = trajectories.getTrajectory(i);
            for (int n = 0; n < trajectory.getNeighbors().size(); n++) {
                // check distance
                if (!trajectories.getDistance(i, n).isValid()) {
                    Trajectory neighbor = trajectory.getNeighbors().getTrajectory(n);
                    TrajectoryWithNeighborhood newTrajectory =  spawn(configuration, trajectory.getReference().getTrajectory(), neighbor.getReference().getTrajectory(), trajectories.getDistance(i, n));
                    if (newTrajectory == null) {
                        continue;
                    }
                    for (Trajectory neigh: newTrajectory.getNeighbors()) {
                        newSecondaryTrajectories.add(neigh);
                    }
                    newTrajectories.add(newTrajectory);
                }

            }
        }
        return new SpawnedDataBlockWrapper(
                new ListDataBlock<>(newTrajectories),
                new AbstractConfiguration(configuration.getInitialSpace()) {
                    @Override
                    public int getStartIndex(int index, int neighborIndex) {
                        return 0;
                    }
                },
                new ListDataBlock<>(new ArrayList<>(newSecondaryTrajectories)));
    }

    @Override
    public SpawnedDataBlock spawn(OrthogonalSpace space) {
        List<TrajectoryWithNeighborhood> result = new ArrayList<>();
        Collection<FractionPoint> extremes = FractionPoint.extremes(space);
        Map<FractionPoint, Trajectory> surroundings = new HashMap<>();
        for (FractionPoint point: extremes) {
            Trajectory main = new PointTrajectory(createPoint(space, point));
            alreadySpawnedPrimaryTrajectories.put(point, main);
            List<Trajectory> neighbors = new ArrayList<>();
            for (FractionPoint n: point.surround(space, new Fraction(1, 2))) {
                if (n.isValid()) {
                    if (surroundings.containsKey(n)) {
                        neighbors.add(surroundings.get(n));
                    } else {
                        Trajectory t = new PointTrajectory(createPoint(space, n));
                        surroundings.put(n, t);
                        alreadySpawnedCollisionTrajectories.put(n, t);
                        neighbors.add(t);
                    }
                }
            }
            result.add(TrajectoryWithNeighborhoodWrapper.createAndUpdateReference(main, new ListDataBlock<>(neighbors)));
        }
        FractionPoint middle = FractionPoint.maximum(space.getDimension()).middle(FractionPoint.minimum(space.getDimension()));
        Trajectory t = new PointTrajectory(createPoint(space, middle));
        result.add(TrajectoryWithNeighborhoodWrapper.createAndUpdateReference(t, new ListDataBlock<>(new ArrayList<>(surroundings.values()))));
        return new SpawnedDataBlockWrapper(new ListDataBlock<>(result),
                new AbstractConfiguration(space) {
                    @Override
                    public int getStartIndex(int index, int neighborIndex) {
                        return 0;
                    }
                }, new ListDataBlock<>(new ArrayList<>(surroundings.values())));
    }

    protected TrajectoryWithNeighborhood spawn(Configuration configuration, Trajectory trajectory, Trajectory neighbor, LimitedDistance distance) {
        ArrayFractionPoint tPoint = (ArrayFractionPoint) trajectory.getFirstPoint();
        ArrayFractionPoint nPoint = (ArrayFractionPoint) neighbor.getFirstPoint();
        FractionPoint middle = tPoint.getFractionPoint().middle(nPoint.getFractionPoint());
        Trajectory middleTrajectory;
        synchronized(alreadySpawnedPrimaryTrajectories) {
            middleTrajectory = alreadySpawnedPrimaryTrajectories.get(middle);
            if (middleTrajectory == null) {
                middleTrajectory = new PointTrajectory(createPoint(configuration.getInitialSpace(), middle));
                alreadySpawnedPrimaryTrajectories.put(middle, middleTrajectory);
            } else {
                return null;
            }
        }
        Fraction radius = tPoint.getFractionPoint().diffDistance(nPoint.getFractionPoint()).divide(2);
        Collection<FractionPoint> surroundings = middle.surround(configuration.getInitialSpace(), radius);
        List<Trajectory> neighbors = new ArrayList<>();
        for (FractionPoint point: surroundings) {
            if (!point.isValid()) {
                continue;
            }
            Trajectory neigh;
            synchronized(alreadySpawnedCollisionTrajectories) {
                neigh = alreadySpawnedCollisionTrajectories.get(point);
                if (neigh == null) {
                    neigh = new PointTrajectory(createPoint(configuration.getInitialSpace(), point));
                    alreadySpawnedCollisionTrajectories.put(point, neigh);
                } else {
                    neigh = neigh.getReference().getTrajectory();
                }
            }
            neighbors.add(neigh);
        }
        return TrajectoryWithNeighborhoodWrapper.createAndUpdateReference(middleTrajectory, new ListDataBlock<>(neighbors));
    }

    protected ArrayFractionPoint createPoint(OrthogonalSpace space, FractionPoint fractionPoint) {
        float[] data = space.getMinBounds().toArrayCopy();
        Point point = fractionPoint.value(space.getSize());
        for (int dim=0; dim<point.getDimension(); dim++) {
            data[dim] += point.getValue(dim);
        }
        return new ArrayFractionPoint(fractionPoint, space.getMinBounds().getTime(), data);
    }

}
