package org.sybila.parasim.computation.simulation.api;

import java.util.Iterator;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ArraySimulatedDataBlock implements SimulatedDataBlock {

    private DataBlock<Trajectory> dataBlock;
    private Status[] statuses;
    
    public ArraySimulatedDataBlock(DataBlock<Trajectory> dataBlock, Status[] statuses) {
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
    public Trajectory getTrajectory(int index) {
        return dataBlock.getTrajectory(index);
    }

    @Override
    public Iterator<Trajectory> iterator() {
        return dataBlock.iterator();
    }
    
    @Override
    public int size() {
        return dataBlock.size();
    }


    
}
