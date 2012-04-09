package org.sybila.parasim.model.trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface TrajectoryNeighborhood<T extends Trajectory> {

    /**
     * Returns a trajectory neighborhood
     *
     * @param trajectory
     * @return trajectories in neighborhood
     */
    DataBlock<T> getNeighbors(Trajectory trajectory);

}
