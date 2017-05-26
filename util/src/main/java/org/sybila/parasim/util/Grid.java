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
package org.sybila.parasim.util;

import java.util.HashMap;
import java.util.Map;

/**
 * An N-dimensional grid of objects.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 * @param T type of objects contained by grid.
 */
public class Grid<T> {

    private Coordinate dimension;
    private Map<Coordinate, T> grid;

    /**
     * Create new empty grid of designated size.
     * This cannot be changed.
     * @param dimension Specifies dimension and grid's size in each dimension.
     */
    public Grid(Coordinate dimension) {
        grid = new HashMap<Coordinate, T>();
        this.dimension = dimension;
    }

    /**
     * Return dimension.
     * @return Dimension of this grid.
     */
    public int getDimension() {
        return dimension.getDimension();
    }

    /**
     * Return size of this grid in each dimension.
     * @return Object containing grid's size in each dimension.
     */
    public Coordinate getDimensions() {
        return dimension;
    }

    /**
     * Return contained object at given coordinate.
     * @param coord Coordinate of object.
     * @return Object at designated coordinate, <code>null</code> when there is none.
     * @throws IllegalArgumentException when given coordinate does not pertain to this grid.
     */
    public T get(Coordinate coord) {
        checkDimension(coord);
        return grid.get(coord);
    }

    /**
     * Puts an object into grid at given coordinate.
     * @param coord Target coordinate.
     * @param target Object to be put to target coordinate.
     * @return Object which was previously on given coordinate.
     * @throws IllegalArgumentException when given coordinate does not pertain to this grid.
     */
    public T put(Coordinate coord, T target) {
        checkDimension(coord);
        return grid.put(coord, target);
    }

    /**
     * Checks whether given coordinate is inside this grid (i.e. indices are not out of bound).
     * @param coord Coordinate to be checked.
     * @throws IllegalArgumentException when given coordinate does not pertain to this grid.
     */
    protected void checkDimension(Coordinate coord) {
        if (coord.getDimension() != dimension.getDimension()) {
            throw new IllegalArgumentException("Input coordinate has wrong dimension: " + coord.getDimension() + " (expected " + dimension.getDimension() + ")");
        }
        for (int i = 0; i < getDimension(); i++) {
            int index = coord.getCoordinate(i);
            if (index < 0) {
                throw new IllegalArgumentException("Negative index of input coordinate.");
            }
            if (index >= getDimensions().getCoordinate(i)) {
                throw new IllegalArgumentException("Input coordinate index out of bound.");
            }
        }
    }
}
