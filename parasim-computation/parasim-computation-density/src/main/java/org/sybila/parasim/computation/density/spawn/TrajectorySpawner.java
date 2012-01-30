package org.sybila.parasim.computation.density.spawn;

import org.sybila.parasim.computation.density.Configuration;
import org.sybila.parasim.computation.density.distancecheck.DistanceCheckedDataBlock;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface TrajectorySpawner<Conf extends Configuration, Out extends SpawnedDataBlock> {
    
    /**
     * Creates new trajectories.
     * WARNING: It updates neighborhood defined in configuration.
     * @param configuration 
     * @param trajectories
     * @return data block with new trajectories
     */
    Out spawn(Conf configuration, DistanceCheckedDataBlock<Trajectory> trajectories);

    /**
     * Creates new trajectories in the given space. Number of trajectories is defined
     * through the numOfSamples parameter.
     * 
     * @param  configuration 
     * @param space 
     * @param numOfSamples 
     */
    Out spawn(Conf configuration, OrthogonalSpace space, int[] numOfSamples);
    
}
