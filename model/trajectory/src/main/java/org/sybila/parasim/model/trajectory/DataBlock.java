package org.sybila.parasim.model.trajectory;

/**
 * Data block is a set of trajectories which can be extended by other information.
 * 
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 * 
 * @param <T> type of trajectories which are placed in data block
 */
public interface DataBlock<T extends Trajectory> extends Iterable<T> {

    /**
     * Returns the trajectory at the specified position in this data block.
     * 
     * @param index index of the trajectory to return 
     * @return the trajectory at the specified position in this data block
     * @throws IndexOutOfBoundsException - if the index is out of range (index < 0 || index >= size())
     */
    T getTrajectory(int index);

    /**
     * Returns the number of trajectories in this data block.  If this data block
     * contains more than <tt>Integer.MAX_VALUE</tt> trajectories, returns
     * <tt>Integer.MAX_VALUE</tt>.
     * 
     * @return the number of trajectories in this data block
     */
    int size();
}
