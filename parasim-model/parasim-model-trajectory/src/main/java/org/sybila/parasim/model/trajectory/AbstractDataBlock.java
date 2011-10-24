package org.sybila.parasim.model.trajectory;

import java.util.Iterator;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractDataBlock<T extends Trajectory> implements DataBlock<T> {
    
    private int size;
    
    protected AbstractDataBlock(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("The size has to be a positive number.");
        }
        this.size = size;
    }
    
    public int getSize() {
        return size;
    }
    
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private int index = 0;
            
            public boolean hasNext() {
                return index < size;
            }

            public T next() {
                T next = getTrajectory(index);
                index++;
                return next;
            }

            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }
    
}
