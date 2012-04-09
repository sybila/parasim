package org.sybila.parasim.model.trajectory;

import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ListDataBlock<T extends Trajectory> implements DataBlock<T> {

    private List<T> trajectories;

    public ListDataBlock(List<T> trajectories) {
        if (trajectories == null) {
            throw new IllegalArgumentException("The parameter trajectories is null.");
        }
        this.trajectories = trajectories;
    }

    @Override
    public T getTrajectory(int index) {
        return trajectories.get(index);
    }

    @Override
    public int size() {
        return trajectories.size();
    }

    @Override
    public Iterator<T> iterator() {
        return trajectories.iterator();
    }
}
