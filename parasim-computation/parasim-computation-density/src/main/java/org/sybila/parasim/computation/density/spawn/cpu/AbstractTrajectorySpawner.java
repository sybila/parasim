package org.sybila.parasim.computation.density.spawn.cpu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sybila.parasim.computation.MapTrajectoryNeighborhood;
import org.sybila.parasim.computation.density.Configuration;
import org.sybila.parasim.computation.density.distancecheck.DistanceCheckedDataBlock;
import org.sybila.parasim.computation.density.spawn.SpawnedDataBlock;
import org.sybila.parasim.computation.density.spawn.SpawnedDataBlockWrapper;
import org.sybila.parasim.computation.density.spawn.TrajectorySpawner;
import org.sybila.parasim.model.distance.Distance;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.ListDataBlock;
import org.sybila.parasim.model.trajectory.PointTrajectory;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * It iterates through all trajectories and their neighbors with invalid distance. For each
 * pair of trajectory and neighbor with invalid distance it tries to spawn new set of trajectories.
 * 
 * The way of spawning is defined in classes extended this abstract class.
 * 
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractTrajectorySpawner implements TrajectorySpawner<Configuration<Trajectory>, SpawnedDataBlock<Trajectory>> {

    @Override
    public SpawnedDataBlock<Trajectory> spawn(Configuration<Trajectory> configuration, DistanceCheckedDataBlock<Trajectory> trajectories) {
        spawnSetup(configuration, trajectories);
        // note spawned trajectories
        List<Trajectory> newTrajectories = new ArrayList<Trajectory>();
        // note trajectory neighborhoods
        Map<Trajectory, DataBlock<Trajectory>> neighborhood = new HashMap<Trajectory, DataBlock<Trajectory>>();
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
                    }
                }

            }
        }
        spawnTearDown(configuration, trajectories);
        return new SpawnedDataBlockWrapper<Trajectory>(new ListDataBlock<Trajectory>(newTrajectories), new MapTrajectoryNeighborhood<Trajectory>(neighborhood));
    }

    @Override
    public SpawnedDataBlock<Trajectory> spawn(OrthogonalSpace space, int... numOfSamples) {
        if (space.getDimension() != numOfSamples.length) {
            throw new IllegalArgumentException("The number of space dimension and length of [numOfSamples] array doesn't match.");
        }
        // distance between two seeds in the grid
        float[] distance = new float[space.getDimension()];
        // compute number of seeds at all
        int numOfSeeds = 1;
        for (int dim = 0; dim < space.getDimension(); dim++) {
            if (numOfSamples[dim] <= 0) {
                throw new IllegalArgumentException("Number of samples has to be a positive number. It doesn't hold in dimension <" + dim + ">");
            }
            if (numOfSamples[dim] > 1) {
                distance[dim] = space.getSize(dim) / (numOfSamples[dim] - 1);
            } else {
                distance[dim] = 0;
            }
            numOfSeeds *= numOfSamples[dim];
        }
        // auxiliary structures
        List<Trajectory> seeds = new ArrayList<Trajectory>(numOfSeeds);
        Map<Trajectory, List<Trajectory>> neighborhoodLists = new HashMap<Trajectory, List<Trajectory>>(numOfSeeds);
        // minBounds is surely seed, so save it
        Trajectory minBoundsTrajectory = new PointTrajectory(space.getMinBounds());
        seeds.add(minBoundsTrajectory);
        neighborhoodLists.put(minBoundsTrajectory, new ArrayList<Trajectory>());
        // generate other seeds
        for (int dim = 0; dim < space.getDimension(); dim++) {
            int numOfOldSeeds = seeds.size();
            for (int seed = 0; seed < numOfOldSeeds; seed++) {
                Trajectory toBeNeighbor = seeds.get(seed);
                for (int sample = 1; sample < numOfSamples[dim]; sample++) {
                    float[] newPoint = seeds.get(seed).getFirstPoint().toArrayCopy();
                    newPoint[dim] += sample * distance[dim];
                    // create a new seed
                    Trajectory newTrajectory = new PointTrajectory(new ArrayPoint(seeds.get(seed).getFirstPoint().getTime(), newPoint));
                    seeds.add(newTrajectory);
                    neighborhoodLists.put(newTrajectory, new ArrayList<Trajectory>());
                    // mark it as a neighbor for the "to be neighbor" trajectory
                    neighborhoodLists.get(toBeNeighbor).add(newTrajectory);
                    // update "to be neighbor" trajectory
                    toBeNeighbor = newTrajectory;
                }
            }
        }
        // transform neigborhood map of lists to the map of data blocks
        Map<Trajectory, DataBlock<Trajectory>> neighborhoodDataBlocks = new HashMap<Trajectory, DataBlock<Trajectory>>(neighborhoodLists.size());
        for (Trajectory key : neighborhoodLists.keySet()) {
            neighborhoodDataBlocks.put(key, new ListDataBlock<Trajectory>(neighborhoodLists.get(key)));
        }
        // return the result
        return new SpawnedDataBlockWrapper<Trajectory>(
                new ListDataBlock<Trajectory>(seeds),
                new MapTrajectoryNeighborhood<Trajectory>(neighborhoodDataBlocks));
    }

    /**
     * This method is executed in the beginning of the {@link AbstractTrajectorySpawner#spawn(org.sybila.parasim.computation.density.Configuration, org.sybila.parasim.computation.density.distancecheck.DistanceCheckedDataBlock) } method.
     * Override it to clean the spawner after the spawning ends.
     * 
     * @param configuration
     * @param trajectories 
     */
    protected void spawnSetup(Configuration<Trajectory> configuration, DistanceCheckedDataBlock<Trajectory> trajectories) {
    }

    /**
     * This method is executed in the end of the {@link AbstractTrajectorySpawner#spawn(org.sybila.parasim.computation.density.Configuration, org.sybila.parasim.computation.density.distancecheck.DistanceCheckedDataBlock) } method.
     * @param configuration
     * @param trajectories 
     */
    protected void spawnTearDown(Configuration<Trajectory> configuration, DistanceCheckedDataBlock<Trajectory> trajectories) {
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

        private Map<Trajectory, DataBlock<Trajectory>> neighborhoods;

        /**
         * Creates result with no trajectory spawned
         */
        public SpawnedResult() {
            this(null);
        }

        public SpawnedResult(Map<Trajectory, DataBlock<Trajectory>> neighborhoods) {
            this.neighborhoods = neighborhoods;
        }

        public boolean containsTrajectories() {
            return neighborhoods != null && !neighborhoods.isEmpty();
        }

        public Map<Trajectory, DataBlock<Trajectory>> getNeighborhoods() {
            return neighborhoods;
        }

        public Collection<Trajectory> getTrajectories() {
            return neighborhoods.keySet();
        }
    }
}
