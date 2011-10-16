package org.sybila.parasim.computation.density.distancecheck;

import org.sybila.parasim.computation.DataBlock;
import org.sybila.parasim.computation.density.Configuration;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface DistanceChecker<Conf extends Configuration, Out extends DistanceCheckedDataBlock> {
    
    /**
     * Checks distances between trajectories in datablock and their neighborhood
     * defined in configuration
     * 
     * @param congfiguration
     * @param trajectories
     * @return 
     */
    Out check(Conf congfiguration, DataBlock<Trajectory> trajectories);
    
}
