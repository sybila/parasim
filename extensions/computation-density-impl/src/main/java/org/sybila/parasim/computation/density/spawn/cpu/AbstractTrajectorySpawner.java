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
import java.util.List;
import java.util.Map;
import org.sybila.parasim.computation.density.api.Configuration;
import org.sybila.parasim.computation.density.api.InitialSampling;
import org.sybila.parasim.computation.density.distancecheck.api.DistanceCheckedDataBlock;
import org.sybila.parasim.computation.density.spawn.api.SpawnedDataBlock;
import org.sybila.parasim.computation.density.spawn.api.SpawnedDataBlockWrapper;
import org.sybila.parasim.computation.density.spawn.api.TrajectorySpawner;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.Distance;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.ListDataBlock;
import org.sybila.parasim.model.trajectory.MapTrajectoryNeighborhood;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.PointTrajectory;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.trajectory.TrajectoryNeighborhood;

/**
 * It iterates through all trajectories and their neighbors with invalid distance. For each
 * pair of trajectory and neighbor with invalid distance it tries to spawn new set of trajectories.
 *
 * The way of spawning is defined in classes extended this abstract class.
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractTrajectorySpawner implements TrajectorySpawner {

    @Override
    public SpawnedDataBlock spawn(Configuration configuration, DistanceCheckedDataBlock trajectories) {
        spawnSetup(configuration, trajectories);
        // note spawned trajectories
        List<Trajectory> newTrajectories = new ArrayList<>();
        // note secondary trajectories
        List<Trajectory> newSecondaryTrajectories = new ArrayList<>();
        // note trajectory neighborhoods
        final Map<Point, DataBlock<Trajectory>> neighborhood = new HashMap<>();
        // iterate through all pairs of trajectory and neighbor with invalid distance
        for (int i = 0; i < trajectories.size(); i++) {
            Trajectory trajectory = trajectories.getTrajectory(i);
            for (int n = 0; n < configuration.getNeighborhood().getNeighbors(trajectory).size(); n++) {
                // check distance
                if (!trajectories.getDistance(i, n).isValid()) {
                    Trajectory neighbor = configuration.getNeighborhood().getNeighbors(trajectory).getTrajectory(n);
                    SpawnedResult spawned = spawnTrajectories(trajectory.getReference().getTrajectory(), neighbor.getReference().getTrajectory(), trajectories.getDistance(i, n));
                    if (spawned.containsTrajectories()) {
                        newTrajectories.addAll(spawned.getTrajectories());
                        neighborhood.putAll(spawned.getNeighborhoods());
                        if (spawned.getSecondaryTrajectories() != null) {
                            newSecondaryTrajectories.addAll(spawned.getSecondaryTrajectories());
                        }
                    }
                }

            }
        }
        spawnTearDown(configuration, trajectories);
        return new SpawnedDataBlockWrapper(
                new ListDataBlock<>(newTrajectories),
                new AbstractConfiguration(configuration.getInitialSampling(), configuration.getInitialSpace()) {
                    private TrajectoryNeighborhood trajectoryNeighborhood = new MapTrajectoryNeighborhood(neighborhood);
                    @Override
                    public TrajectoryNeighborhood getNeighborhood() {
                        return trajectoryNeighborhood;
                    }
                    @Override
                    public int getStartIndex(int index, int neighborIndex) {
                        return 0;
                    }
                },
                new ListDataBlock<>(newSecondaryTrajectories));
    }

    @Override
    public SpawnedDataBlock spawn(OrthogonalSpace space, InitialSampling initialSampling) {
        // compute distances to sample
        float[] sampleDistances = new float[initialSampling.getDimension()];
        for (int dim=0; dim<sampleDistances.length; dim++) {
            if (initialSampling.getNumberOfSamples(dim) >= 1)  {
                sampleDistances[dim] = Math.abs(space.getMinBounds().getValue(dim) - space.getMaxBounds().getValue(dim)) / (initialSampling.getNumberOfSamples(dim) - 1);
            } else {
                sampleDistances[dim] = -1;
            }
        }
        List<Trajectory> primaryTrajectories = new ArrayList<>();
        primaryTrajectories.add(new PointTrajectory(space.getMinBounds()));
        // compute primary trajectories
        for (int dim=0; dim<sampleDistances.length; dim++) {
            if (sampleDistances[dim] == -1) {
                continue;
            }
            int numOfOldSeeds = primaryTrajectories.size();
            for (int seed = 0; seed < numOfOldSeeds; seed++) {
                for (int sample = 1; sample < initialSampling.getNumberOfSamples(dim); sample++) {
                    float[] newPoint = primaryTrajectories.get(seed).getFirstPoint().toArrayCopy();
                    newPoint[dim] += sample * sampleDistances[dim];
                    Trajectory newTrajectory = new PointTrajectory(new ArrayPoint(primaryTrajectories.get(seed).getFirstPoint().getTime(), newPoint));
                    primaryTrajectories.add(newTrajectory);
                }
            }
        }
        // compute secondary trajectories
        final Map<Point, DataBlock<Trajectory>> neighborhoods = new HashMap<>();
        Map<Point, Trajectory> cache = new HashMap<>();
        for (Trajectory t: primaryTrajectories) {
            List<Trajectory> neighborhood = new ArrayList<>();
            for (int dim=0; dim<sampleDistances.length; dim++) {
                if (sampleDistances[dim] == -1) {
                    continue;
                }
                float[] positiveNeighborArray = t.getFirstPoint().toArrayCopy();
                float[] negativeNeighborArray = t.getFirstPoint().toArrayCopy();
                positiveNeighborArray[dim] += sampleDistances[dim] / 2;
                negativeNeighborArray[dim] -= sampleDistances[dim] / 2;
                Point positiveNeighbor = new ArrayPoint(t.getFirstPoint().getTime(), positiveNeighborArray);
                Point negativeNeighbor = new ArrayPoint(t.getFirstPoint().getTime(), negativeNeighborArray);
                if (!cache.containsKey(positiveNeighbor)) {
                    cache.put(positiveNeighbor, new PointTrajectory(positiveNeighbor));
                }
                if (!cache.containsKey(negativeNeighbor)) {
                    cache.put(negativeNeighbor, new PointTrajectory(negativeNeighbor));
                }
                neighborhood.add(cache.get(positiveNeighbor));
                neighborhood.add(cache.get(negativeNeighbor));
            }
            neighborhoods.put(t.getFirstPoint(), new ListDataBlock<>(neighborhood));
        }
        List<Trajectory> secondaryTrajectories = new ArrayList<>(cache.values());

        return new SpawnedDataBlockWrapper(
                new ListDataBlock<>(primaryTrajectories),
                new AbstractConfiguration(initialSampling, space) {
                    private final TrajectoryNeighborhood trajectoryNeighborhood = new MapTrajectoryNeighborhood(neighborhoods);

                    @Override
                    public TrajectoryNeighborhood getNeighborhood() {
                        return trajectoryNeighborhood;
                    }
                    @Override
                    public int getStartIndex(int index, int neighborIndex) {
                        return 0;
                    }
                },
                new ListDataBlock<>(secondaryTrajectories));
    }

    /**
     * This method is executed in the beginning of the {@link AbstractTrajectorySpawner#spawn(org.sybila.parasim.computation.density.Configuration, org.sybila.parasim.computation.density.distancecheck.DistanceCheckedDataBlock) } method.
     * Override it to clean the spawner after the spawning ends.
     *
     * @param configuration
     * @param trajectories
     */
    protected void spawnSetup(Configuration configuration, DistanceCheckedDataBlock trajectories) {
    }

    /**
     * This method is executed in the end of the {@link AbstractTrajectorySpawner#spawn(org.sybila.parasim.computation.density.Configuration, org.sybila.parasim.computation.density.distancecheck.DistanceCheckedDataBlock) } method.
     * @param configuration
     * @param trajectories
     */
    protected void spawnTearDown(Configuration configuration, DistanceCheckedDataBlock trajectories) {
    }

    /**
     * Override to provide trajectory spawning.
     *
     * WARNING: This method has to be consistent with the {@link AbstractTrajectorySpawner#createNeighborhoods(org.sybila.parasim.model.trajectory.Trajectory, org.sybila.parasim.model.trajectory.Trajectory, org.sybila.parasim.model.distance.Distance) }.
     *
     * @param trajectory
     * @param neighbor
     * @param distance
     *
     * @return collection containing spawned trajectories
     */
    protected abstract SpawnedResult spawnTrajectories(Trajectory trajectory, Trajectory neighbor, Distance distance);

    protected static class SpawnedResult {

        private Map<Point, DataBlock<Trajectory>> neighborhoods;
        private Collection<Trajectory> secondaryTrajectories;
        private Collection<Trajectory> primaryTrajectories;

        /**
         * Creates result with no trajectory spawned
         */
        public SpawnedResult() {
            this(null, null, null);
        }

        public SpawnedResult(Map<Point, DataBlock<Trajectory>> neighborhoods, Collection<Trajectory> primaryTrajectories, Collection<Trajectory> secondaryTrajectories) {
            this.neighborhoods = neighborhoods;
            this.secondaryTrajectories = secondaryTrajectories;
            this.primaryTrajectories = primaryTrajectories;
        }

        public boolean containsTrajectories() {
            return neighborhoods != null && !neighborhoods.isEmpty();
        }

        public Map<Point, DataBlock<Trajectory>> getNeighborhoods() {
            return neighborhoods;
        }

        public Collection<Trajectory> getSecondaryTrajectories() {
            return secondaryTrajectories;
        }

        public Collection<Trajectory> getTrajectories() {
            return primaryTrajectories;
        }
    }
}
