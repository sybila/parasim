package org.sybila.parasim.model.trajectory;

/**
 * Enables appending and adding to the DataBlock.
 * 
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public interface MutableDataBlock extends DataBlock<Trajectory>
{
    /**
     * Appends all the trajectories to the end of the ones already in the
     * DataBlock. If the number of trajectories to be appended and the ones
     * already in DataBlock differ or they are not Mutable a exception is thrown.
     *
     * @param trajectories Trajectories to be appended.
     */
    void append(DataBlock<Trajectory> trajectories);

    /**
     * Adds all the trajectories into the DataBlock.
     * @param trajectories Trajectories to be added.
     */
    void merge(DataBlock<Trajectory> trajectories);

}
