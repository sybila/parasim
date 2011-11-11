package org.sybila.parasim.computation.cycledetection.cpu;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.sybila.parasim.model.trajectory.Point;

/**
 * Represent a cyclic queue with given capacity. One it's capacity is filled
 * adding new items will overwrite the oldes ones.
 * 
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public class ExtremesQueue implements Iterable {

    private Point[] fifo;
    private int first;
    private int count;

    public ExtremesQueue(int capacity)
    {
        if (capacity < 1)
        {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        fifo = new Point[capacity];
        first = 0;
        count = 0;
    }

    public int getCapacity()
    {
        return fifo.length;
    }

    public void add(Point p)
    {
       if (count < fifo.length)
       {
           fifo[count] = p;
           count++;
           first++;
       }
       else
       {
           first = (first + 1) % fifo.length;
           fifo[first] = p;
       }
    }

    @Override
    public Iterator<Point> iterator()
    {
       return new ExtremesQueueIterator();
    }

    private class ExtremesQueueIterator implements Iterator<Point>
    {
        private int index;

        ExtremesQueueIterator()
        {
            index = -1;
        }

        @Override
        public boolean hasNext()
        {
            if (index < count-1)
            {
                return true;
            }
            else return false;
        }

        @Override
        public Point next()
        {
            if (!hasNext())
            {
                throw new NoSuchElementException("next Point doesn't exist");
            }
            index++;
            return fifo[(first-index+fifo.length) % fifo.length];
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("remove() not supported");
        }
    }

}
