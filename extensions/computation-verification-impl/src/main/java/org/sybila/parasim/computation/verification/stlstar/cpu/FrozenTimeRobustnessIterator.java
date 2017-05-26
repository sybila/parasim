/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.computation.verification.stlstar.cpu;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.computation.verification.cpu.StarMonitor;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.util.Coordinate;

/**
 * This iterator iterates over a STL* monitor in time for given frozen times.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
class FrozenTimeRobustnessIterator implements Iterator<Robustness> {

    private final Coordinate.Builder coord;
    private final int size;
    private final StarMonitor monitor;
    private int nextPosition;

    private void setCoordinate(int newPosition) {
        nextPosition = newPosition;
        coord.setCoordinate(0, newPosition);
    }

    /**
     * Create iterator over monitor.
     *
     * @param monitor Monitor to be iterated over.
     * @param frozen Frozen time values (first value does not matter).
     */
    public FrozenTimeRobustnessIterator(StarMonitor monitor, Coordinate frozen) {
        Validate.notNull(monitor);
        Validate.notNull(frozen);
        this.monitor = monitor;
        size = monitor.size();
        coord = new Coordinate.Builder(frozen);
        setCoordinate(0);
    }

    @Override
    public boolean hasNext() {
        return nextPosition < size;
    }

    @Override
    public Robustness next() {
        if (!hasNext()) {
            throw new NoSuchElementException("Iterator has no other field.");
        }
        Robustness result = monitor.getRobustness(coord.create());
        setCoordinate(nextPosition + 1);
        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("You cannot remove from this iterator.");
    }
}
