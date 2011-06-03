/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim.computation.simulation;

import java.util.Iterator;
import java.util.NoSuchElementException;
import parasim.computation.Point;

/**
 *
 * @author Sven Dražan <sven@mail.muni.cz>
 */
public class ExtremesQueue implements Iterable
{    
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

        public boolean hasNext()
        {
            if (index < count-1)
            {
                return true;
            }
            else return false;
        }

        public Point next()
        {
            if (!hasNext())
            {
                throw new NoSuchElementException("next Point doesn't exist");
            }
            index++;
            return fifo[(first-index+fifo.length) % fifo.length];
        }

        public void remove()
        {
            throw new UnsupportedOperationException("remove() not supported");
        }
    }

}
