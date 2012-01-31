package org.sybila.parasim.model.trajectory;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class EmptyDataBlock<T extends Trajectory> implements DataBlock<T> {

    public static final EmptyDataBlock<Trajectory> EMPTY_DATA_BLOCK = new EmptyDataBlock<Trajectory>();
    
    public T getTrajectory(int index) {
        throw new IndexOutOfBoundsException();
    }

    public int size() {
        return 0;
    }

    public Iterator<T> iterator() {
        return new Iterator<T>() {

            public boolean hasNext() {
                return false;
            }

            public T next() {
                throw new NoSuchElementException();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    
    
}
