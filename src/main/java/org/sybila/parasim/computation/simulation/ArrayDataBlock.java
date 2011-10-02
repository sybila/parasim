package org.sybila.parasim.computation.simulation;

import java.util.Iterator;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ArrayDataBlock<T extends Trajectory> implements DataBlock<T> {

    private org.sybila.parasim.computation.DataBlock<T> dataBlock;
    private Status[] statuses;
    
    public ArrayDataBlock(org.sybila.parasim.computation.DataBlock<T> dataBlock, Status[] statuses) {
        if (dataBlock == null) {
            throw new IllegalArgumentException("The parameter dataBlock is null.");
        }
        if (statuses == null) {
            throw new IllegalArgumentException("The parameter statuses is null.");
        }
        if (dataBlock.size() != statuses.length) {
            throw new IllegalArgumentException("The number of trajectories in data block doesn't match with the number statuses.");
        }
        this.dataBlock = dataBlock;
        this.statuses = statuses;
    }

    @Override
    public Status getStatus(int index) {
        return statuses[index];
    }    
    
    @Override
    public T getTrajectory(int index) {
        return dataBlock.getTrajectory(index);
    }

    @Override
    public Iterator<T> iterator() {
        return dataBlock.iterator();
    }
    
    @Override
    public int size() {
        return dataBlock.size();
    }


    
}
