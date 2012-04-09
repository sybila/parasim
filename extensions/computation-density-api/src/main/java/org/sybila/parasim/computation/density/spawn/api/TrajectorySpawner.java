package org.sybila.parasim.computation.density.spawn.api;

import org.sybila.parasim.computation.density.api.Configuration;
import org.sybila.parasim.computation.density.distancecheck.api.DistanceCheckedDataBlock;
import org.sybila.parasim.model.space.OrthogonalSpace;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface TrajectorySpawner {

    /**
     * Creates new trajectories.
     * WARNING: It updates neighborhood defined in configuration.
     * @param configuration
     * @param trajectories
     * @return data block with new trajectories
     */
    SpawnedDataBlock spawn(Configuration configuration, DistanceCheckedDataBlock trajectories);

    /**
     * Creates new trajectories in the given space. Number of trajectories is defined
     * through the numOfSamples parameter.
     *
     * @param  configuration
     * @param space
     * @param numOfSamples
     */
    SpawnedDataBlock spawn(OrthogonalSpace space, int... numOfSamples);
}
