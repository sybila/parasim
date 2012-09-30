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
package org.sybila.parasim.model.trajectory;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Data block is a set of trajectories which can be extended by other information.
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 *
 * @param <T> type of trajectories which are placed in data block
 */
public interface DataBlock<T extends Trajectory> extends Iterable<T>, Serializable {

    public static DataBlock EMPTY_DATABLOCK = new EmptyDatablock();

    /**
     * Returns the trajectory at the specified position in this data block.
     *
     * @param index index of the trajectory to return
     * @return the trajectory at the specified position in this data block
     * @throws IndexOutOfBoundsException - if the index is out of range (index < 0 || index >= size())
     */
    T getTrajectory(int index);

    /**
     * Returns the number of trajectories in this data block.  If this data block
     * contains more than <tt>Integer.MAX_VALUE</tt> trajectories, returns
     * <tt>Integer.MAX_VALUE</tt>.
     *
     * @return the number of trajectories in this data block
     */
    int size();

    final class EmptyDatablock<T extends Trajectory> implements DataBlock<T> {

        private EmptyDatablock() {
        }

        @Override
        public T getTrajectory(int index) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                @Override
                public boolean hasNext() {
                    return false;
                }
                @Override
                public T next() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
                @Override
                public void remove() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            };
        }

    }
}