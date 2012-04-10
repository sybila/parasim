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
                return trajectories[position - 1];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }
}
