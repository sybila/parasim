/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
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

import java.util.Collection;
import java.util.Collections;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class PointWithNeigborhoodWrapper extends AbstractPoint implements PointWithNeighborhood {

    private final Point wrapped;
    private final Collection<Point> neighbors;

    public PointWithNeigborhoodWrapper(Point point, Collection<Point> neighbors) {
        super(point.getDimension(), point.getTime());
        this.wrapped = point;
        this.neighbors = Collections.unmodifiableCollection(neighbors);
    }

    public PointWithNeigborhoodWrapper(Point point) {
        this(point, Collections.EMPTY_LIST);
    }

    @Override
    public float getValue(int index) {
        return wrapped.getValue(index);
    }

    @Override
    public Collection<Point> getNeighbors() {
        return neighbors;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public Point unwrap() {
        return wrapped;
    }

}
