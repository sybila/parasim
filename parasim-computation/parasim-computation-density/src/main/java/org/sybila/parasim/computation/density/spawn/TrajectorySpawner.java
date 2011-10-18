package org.sybila.parasim.computation.density.spawn;

import org.sybila.parasim.computation.density.Configuration;
import org.sybila.parasim.computation.density.distancecheck.DistanceCheckedDataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface TrajectorySpawner<Conf extends Configuration, Out extends SpawnedDataBlock> {
    
    /**
     * Create new trajectories.
     * WARNING: It updates neighborhood defined in configuration.
     * @param configuration 
     * @param trajectories
     * @return data block with new trajectories
     */
    Out spawn(Conf configuration, DistanceCheckedDataBlock<Trajectory> trajectories);
    
}
