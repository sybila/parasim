package org.sybila.parasim.computation.distancechecking;

import java.util.Iterator;
import java.util.List;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ListDataBlock<T extends Trajectory> implements DataBlock<T> {
    
    private org.sybila.parasim.computation.DataBlock<T> dataBlock;
    private List<List<Float>> distances;
    
    public ListDataBlock(org.sybila.parasim.computation.DataBlock<T> dataBlock, List<List<Float>> distances) {
        if (dataBlock == null) {
            throw new IllegalArgumentException("The parameter dataBlock is null.");
        }
        if (distances == null) {
            throw new IllegalArgumentException("The parameter distances is null.");
        }
        if (distances.size() != dataBlock.size()) {
            throw new IllegalArgumentException("The number of distances doesn't correspond to the number of trajectories.");
        }
        this.dataBlock = dataBlock;
        this.distances = distances;
    }

    @Override
    public List<Float> getDistances(int index) {
        return distances.get(index);
    }

    @Override
    public T getTrajectory(int index) {
        return dataBlock.getTrajectory(index);
    }

    @Override
    public int size() {
        return dataBlock.size();
    }

    @Override
    public Iterator<T> iterator() {
        return dataBlock.iterator();
    }
    
}
