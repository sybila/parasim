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

/**
 * Represents a single simulated point in time and space.
 *
 * @author <a href="mailto:xdrazan@fi.muni.cz">Sven Drazan</a>
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Point extends Iterable<Float> {

    /**
     * @return Number of dimensions of given point.
     */
    int getDimension();

    /**
     * @return time of the point
     */
    float getTime();

    /**
     * @param index The dimension of who's value to return.
     * @return Value of given dimension.
     */
    float getValue(int index);

    /**
     * This method returns always the same instance of array (in opposite to {@link  Point#toArrayCopy()).
     *
     * @return Values of all dimensions as an array without time
     */
    float[] toArray();

    float[] toArray(int numOfDimensions);

    /**
     * This method always returns a new instance of array (in opposite to {@link Point#toArray()}).
     *
     * @return Values of all dimension as an array without time
     */
    float[] toArrayCopy();

    float[] toArrayCopy(int numOfDimensions);
}
