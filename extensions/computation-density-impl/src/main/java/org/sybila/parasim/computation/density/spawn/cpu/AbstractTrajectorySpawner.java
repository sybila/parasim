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
import java.util.Map.Entry;
import org.sybila.parasim.computation.density.api.Configuration;
import org.sybila.parasim.computation.density.api.InitialSampling;
import org.sybila.parasim.computation.density.distancecheck.api.DistanceCheckedDataBlock;
import org.sybila.parasim.computation.density.spawn.api.SpawnedDataBlock;
import org.sybila.parasim.computation.density.spawn.api.SpawnedDataBlockWrapper;
import org.sybila.parasim.computation.density.spawn.api.TrajectorySpawner;
import org.sybila.parasim.model.trajectory.Distance;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.trajectory.ArrayPoint;
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
        List<Trajectory> newTrajectories = new ArrayList<Trajectory>();
        // note secondary trajectories
        List<Trajectory> newSecondaryTrajectories = new ArrayList<Trajectory>();
        // note trajectory neighborhoods
        final Map<Point, DataBlock<Trajectory>> neighborhood = new HashMap<Point, DataBlock<Trajectory>>();
        // iterate through all pairs of trajectory and neighbor with invalid distance
        for (int i = 0; i < trajectories.size(); i++) {
            Trajectory trajectory = trajectories.getTrajectory(i);
            for (int n = 0; n < configuration.getNeighborhood().getNeighbors(trajectory).size(); n++) {
                // check distance
                if (!trajectories.getDistance(i, n).isValid()) {
                    Trajectory neighbor = configuration.getNeighborhood().getNeighbors(trajectory).getTrajectory(n);
                    SpawnedResult spawned = spawnTrajectories(trajectory, neighbor, trajectories.getDistance(i, n));
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
                new ListDataBlock<Trajectory>(newTrajectories),
                new AbstractConfiguration(configuration.getInitialSampling(), configuration.getInitialSpace()) {
                    private TrajectoryNeighborhood trajectoryNeighborhood = new MapTrajectoryNeighborhood(neighborhood);
                    public TrajectoryNeighborhood getNeighborhood() {
                        return trajectoryNeighborhood;
                    }
                    public int getStartIndex(int index, int neighborIndex) {
                        return 0;
                    }
                },
                new ListDataBlock<Trajectory>(newSecondaryTrajectories));
    }

    @Override
    public SpawnedDataBlock spawn(OrthogonalSpace space, InitialSampling initialSampling) {
        if (space.getDimension() != initialSampling.getDimension()) {
            throw new IllegalArgumentException("The number of space dimension and length of [numOfSamples] array doesn't match.");
        }
        // distance between two seeds in the grid
        float[] distance = new float[space.getDimension()];
        // compute number of seeds at all
        int numOfSeeds = 1;
        for (int dim = 0; dim < space.getDimension(); dim++) {
            if (initialSampling.getNumberOfSamples(dim) > 1) {
                distance[dim] = space.getSize(dim) / (initialSampling.getNumberOfSamples(dim) - 1);
            } else {
                distance[dim] = 0;
            }
            numOfSeeds *= initialSampling.getNumberOfSamples(dim);
        }
        // auxiliary structures
        List<Trajectory> seeds = new ArrayList<Trajectory>(numOfSeeds / 2);
        List<Trajectory> secondarySeeds = new ArrayList<Trajectory>(numOfSeeds / 2);
        List<Trajectory> allSeeds = new ArrayList<Trajectory>(numOfSeeds);
        Map<Trajectory, Boolean> allSeedsMap = new HashMap<Trajectory, Boolean>(numOfSeeds);
        Map<Trajectory, List<Trajectory>> neighborhoodLists = new HashMap<Trajectory, List<Trajectory>>(numOfSeeds / 2);
        // minBounds is surely seed, so save it
        Trajectory minBoundsTrajectory = new PointTrajectory(space.getMinBounds());
        allSeeds.add(minBoundsTrajectory);
        allSeedsMap.put(minBoundsTrajectory, Boolean.FALSE);
        neighborhoodLists.put(minBoundsTrajectory, new ArrayList<Trajectory>());
        // generate other seeds
        for (int dim = 0; dim < space.getDimension(); dim++) {
            int numOfOldSeeds = allSeeds.size();
            for (int seed = 0; seed < numOfOldSeeds; seed++) {
                Trajectory toBeNeighbor = allSeeds.get(seed);
                for (int sample = 1; sample < initialSampling.getNumberOfSamples(dim); sample++) {
                    float[] newPoint = allSeeds.get(seed).getFirstPoint().toArrayCopy();
                    newPoint[dim] += sample * distance[dim];
                    // create a new seed
                    Trajectory newTrajectory = new PointTrajectory(new ArrayPoint(allSeeds.get(seed).getFirstPoint().getTime(), newPoint));
                    allSeeds.add(newTrajectory);
                    allSeedsMap.put(newTrajectory, !allSeedsMap.get(toBeNeighbor));
                    neighborhoodLists.put(newTrajectory, new ArrayList<Trajectory>());
                    // mark it as a neighbor for the "to be neighbor" trajectory
                    neighborhoodLists.get(toBeNeighbor).add(newTrajectory);
                    // update "to be neighbor" trajectory
                    toBeNeighbor = newTrajectory;
                }
            }
        }
        // reorganize data
        for (Entry<Trajectory, Boolean> entry : allSeedsMap.entrySet()) {
            if (entry.getValue()) {
                seeds.add(entry.getKey());
            } else {
                secondarySeeds.add(entry.getKey());
                for (Trajectory master : neighborhoodLists.get(entry.getKey())) {
                    neighborhoodLists.get(master).add(entry.getKey());
                }
                neighborhoodLists.remove(entry.getKey());
            }
        }
        // transform neigborhood map of lists to the map of data blocks
        final Map<Point, DataBlock<Trajectory>> neighborhoodDataBlocks = new HashMap<Point, DataBlock<Trajectory>>(neighborhoodLists.size());
        for (Trajectory key : neighborhoodLists.keySet()) {
            neighborhoodDataBlocks.put(key.getFirstPoint(), new ListDataBlock<Trajectory>(neighborhoodLists.get(key)));
        }
        // return the result
        return new SpawnedDataBlockWrapper(
                new ListDataBlock<Trajectory>(seeds),
                new AbstractConfiguration(initialSampling, space) {
                    private TrajectoryNeighborhood trajectoryNeighborhood = new MapTrajectoryNeighborhood(neighborhoodDataBlocks);
                    public int getStartIndex(int index, int neighborIndex) {
                        return 0;
                    }
                    public TrajectoryNeighborhood getNeighborhood() {
                        return trajectoryNeighborhood;
                    }
                },
                new ListDataBlock<Trajectory>(secondarySeeds));
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
