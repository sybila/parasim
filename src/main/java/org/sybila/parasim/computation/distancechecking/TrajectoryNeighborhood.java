package org.sybila.parasim.computation.distancechecking;

import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface TrajectoryNeighborhood<T extends Trajectory> {
    
    /**
     * Returns trajectory neighborhood
     * 
     * @param trajectory
     * @return trajectories in neighborhood
     */
    org.sybila.parasim.computation.DataBlock<T> getNeighbors(Trajectory trajectory);
    
}
