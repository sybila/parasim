package org.sybila.parasim.model.trajectory;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ArrayDataBlock<T extends Trajectory> implements DataBlock<T> {

    private T[] trajectories;
    
    public ArrayDataBlock(T[] trajectories) {
        if (trajectories == null) {
            throw new IllegalArgumentException("The parameter trajectories is null.");
        }
        if (trajectories.length == 0) {
            throw new IllegalArgumentException("The number of trajectories is 0.");
        }
        this.trajectories = trajectories;
    }
    
    @Override
    public T getTrajectory(int index) {
        return trajectories[index];
    }

    @Override
    public int size() {
        return trajectories.length;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private int position = 0;
            
            @Override
            public boolean hasNext() {
                return position < trajectories.length;
            }

            @Override
            public T next() {
                if (position >= trajectories.length) {
                    throw new NoSuchElementException("There is no other trajectory.");
                }
                position++;
                return trajectories[position-1];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
            
        };
    }
    
}
