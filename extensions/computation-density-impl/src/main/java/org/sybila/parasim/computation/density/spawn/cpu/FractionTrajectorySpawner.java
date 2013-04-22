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
package org.sybila.parasim.computation.density.spawn.cpu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.computation.density.api.Configuration;
import org.sybila.parasim.computation.density.distancecheck.api.DistanceCheckedDataBlock;
import org.sybila.parasim.computation.density.spawn.api.SpawnedDataBlock;
import org.sybila.parasim.computation.density.spawn.api.SpawnedDataBlockWrapper;
import org.sybila.parasim.computation.density.spawn.api.SpawnedTrajectoriesCache;
import org.sybila.parasim.computation.density.spawn.api.TrajectorySpawner;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.trajectory.ArrayFractionPoint;
import org.sybila.parasim.model.trajectory.FractionPoint;
import org.sybila.parasim.model.trajectory.FractionPoint.Fraction;
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

    private final SpawnedTrajectoriesCache primaryCache;
    private final SpawnedTrajectoriesCache secondaryCache;

    private static final Logger LOGGER = LoggerFactory.getLogger(FractionTrajectorySpawner.class);

    public FractionTrajectorySpawner(SpawnedTrajectoriesCache primaryCache, SpawnedTrajectoriesCache secondaryCache) {
        this.primaryCache = primaryCache;
        this.secondaryCache = secondaryCache;
    }

    @Override
    public SpawnedDataBlock spawn(Configuration configuration, DistanceCheckedDataBlock trajectories) {
        LogInformation logInformation = new LogInformation();
        boolean[] dimensionsToSkip = new boolean[configuration.getInitialSpace().getDimension()];
        for (int dim=0; dim<dimensionsToSkip.length; dim++) {
            dimensionsToSkip[dim]= configuration.getInitialSpace().getMinBounds().getValue(dim) == configuration.getInitialSpace().getMaxBounds().getValue(dim);
        }
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
                    TrajectoryWithNeighborhood newTrajectory =  spawn(configuration, trajectory.getReference().getTrajectory(), neighbor.getReference().getTrajectory(), trajectories.getDistance(i, n), dimensionsToSkip, logInformation);
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
        logInformation.log();
        return new SpawnedDataBlockWrapper(
                new ListDataBlock<>(newTrajectories),
                new DelegatingConfiguration(configuration.getInitialSpace()),
                new ListDataBlock<>(new ArrayList<>(newSecondaryTrajectories)));
    }

    @Override
    public SpawnedDataBlock spawn(OrthogonalSpace space) {
        LogInformation logInformation = new LogInformation();
        boolean[] dimensionsToSkip = new boolean[space.getDimension()];
        boolean allSkip = true;
        for (int dim=0; dim<dimensionsToSkip.length; dim++) {
            dimensionsToSkip[dim]= space.getMinBounds().getValue(dim) == space.getMaxBounds().getValue(dim);
            if (!dimensionsToSkip[dim]) {
                allSkip = false;
            }
        }
        List<TrajectoryWithNeighborhood> result = new ArrayList<>();
        Collection<FractionPoint> extremes = FractionPoint.extremes(space.getDimension(), dimensionsToSkip);
        Map<FractionPoint, Trajectory> surroundings = new HashMap<>();
        for (FractionPoint point: extremes) {
            Trajectory main = new PointTrajectory(createPoint(space, point));
            logInformation.spawnPrimaryTrajectory(!primaryCache.store(point, main));
            List<Trajectory> neighbors = new ArrayList<>();
            for (FractionPoint n: point.surround(new Fraction(1, 2), dimensionsToSkip)) {
                if (n.isValid()) {
                    if (surroundings.containsKey(n)) {
                        neighbors.add(surroundings.get(n));
                    } else {
                        Trajectory t = new PointTrajectory(createPoint(space, n));
                        surroundings.put(n, t);
                        logInformation.spawnSecondaryTrajectory(!secondaryCache.store(n, t));
                        neighbors.add(t);
                    }
                }
            }
            result.add(TrajectoryWithNeighborhoodWrapper.createAndUpdateReference(main, new ListDataBlock<>(neighbors)));
        }
        if (!allSkip) {
            FractionPoint middle = FractionPoint.maximum(space.getDimension()).middle(FractionPoint.minimum(space.getDimension()));
            Trajectory t = new PointTrajectory(createPoint(space, middle));
            result.add(TrajectoryWithNeighborhoodWrapper.createAndUpdateReference(t, new ListDataBlock<>(new ArrayList<>(surroundings.values()))));
        }
        logInformation.log();
        return new SpawnedDataBlockWrapper(
                new ListDataBlock<>(result),
                new DelegatingConfiguration(space),
                new ListDataBlock<>(new ArrayList<>(surroundings.values())));
    }

    protected TrajectoryWithNeighborhood spawn(Configuration configuration, Trajectory trajectory, Trajectory neighbor, LimitedDistance distance, boolean[] skip, LogInformation logInformation) {
        ArrayFractionPoint tPoint = (ArrayFractionPoint) trajectory.getFirstPoint();
        ArrayFractionPoint nPoint = (ArrayFractionPoint) neighbor.getFirstPoint();
        FractionPoint middle = tPoint.getFractionPoint().middle(nPoint.getFractionPoint());
        Trajectory middleTrajectory = new PointTrajectory(createPoint(configuration.getInitialSpace(), middle));
        if (!primaryCache.store(middle, middleTrajectory)) {
            logInformation.spawnPrimaryTrajectory(true);
            return null;
        }
        logInformation.spawnPrimaryTrajectory(false);
        Fraction radius = tPoint.getFractionPoint().diffDistance(nPoint.getFractionPoint()).divide(2);
        Collection<FractionPoint> surroundings = middle.surround(radius, skip);
        List<Trajectory> neighbors = new ArrayList<>();
        for (FractionPoint point: surroundings) {
            if (!point.isValid()) {
                continue;
            }
            Trajectory candidate = new PointTrajectory(createPoint(configuration.getInitialSpace(), point));
            Trajectory neigh = secondaryCache.load(point, candidate);
            logInformation.spawnSecondaryTrajectory(neigh != candidate);
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

    public static class DelegatingConfiguration extends AbstractConfiguration implements Serializable {

        public DelegatingConfiguration(OrthogonalSpace initialSpace) {
            super(initialSpace);
        }

        @Override
        public int getStartIndex(int index, int neighborIndex) {
            return 0;
        }

    }

    protected static class LogInformation {

        private long secondaryCacheHit = 0;
        private long secondaryCacheMiss = 0;

        private long primaryCacheHit = 0;
        private long primaryCacheMiss = 0;

        public void spawnSecondaryTrajectory(boolean cacheHit) {
            if (cacheHit) {
                this.secondaryCacheHit++;
            } else {
                this.secondaryCacheMiss++;
            }
        }

        public void spawnPrimaryTrajectory(boolean cacheHit) {
            if (cacheHit) {
                this.primaryCacheHit++;
            } else {
                this.primaryCacheMiss++;
            }
        }

        public long getSecondaryCacheHits() {
            return secondaryCacheHit;
        }

        public long getSecondaryCacheMisses() {
            return secondaryCacheMiss;
        }

        public long getPrimaryCacheHits() {
            return primaryCacheHit;
        }

        public long getPrimaryCacheMisses() {
            return primaryCacheMiss;
        }

        public void log() {
            LOGGER.info("spawning: <{}> primary cache hits, <{}> primary cache misses, <{}> secondary cache hits, <{}> secondary cache misses", primaryCacheHit, primaryCacheMiss, secondaryCacheHit, secondaryCacheMiss);
        }

    }

}
