/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.computation.cycledetection.cpu;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.sybila.parasim.model.trajectory.Point;

/**
 * Represent a cyclic queue with given capacity. One it's capacity is filled
 * adding new items will overwrite the oldes ones.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public class ExtremesQueue implements Iterable {

    private Point[] fifo;
    private int[] indexes;
    private int first;
    private int count;

    public ExtremesQueue(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        fifo = new Point[capacity];
        indexes = new int[capacity];
        first = 0;
        count = 0;
    }

    public int getCapacity() {
        return fifo.length;
    }

    public void add(Point p, int index) {
        if (count < fifo.length) {
            fifo[count] = p;
            indexes[count] = index;
            count++;
            first++;
        } else {
            first = (first + 1) % fifo.length;
            fifo[first] = p;
            indexes[first] = index;
        }
    }

    @Override
    public ExtremesQueueIterator iterator() {
        return new ExtremesQueueIterator();
    }

    public class ExtremesQueueIterator implements Iterator<Point> {

        private int index;

        ExtremesQueueIterator() {
            index = -1;
        }

        @Override
        public boolean hasNext() {
            if (count > 0 && index < count - 1) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public Point next() {
            if (!hasNext()) {
                throw new NoSuchElementException("next Point doesn't exist");
            }
            index++;
            return fifo[(first - index + fifo.length) % fifo.length];
        }

        /**
         * Returns the index of the point that was last returned by next().
         * If next() has not been called yet a runtime exception is thrown.
         *
         * @return Index of point last returned by next().
         */
        public int getIndex() {
            if (index == -1) {
                throw new RuntimeException("Next() has not been called yet.");
            }
            return indexes[(first - index + fifo.length) % fifo.length];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove() not supported");
        }
    }
}
