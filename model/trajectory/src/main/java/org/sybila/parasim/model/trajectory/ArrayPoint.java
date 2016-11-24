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
package org.sybila.parasim.model.trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ArrayPoint extends AbstractPoint {

    private float[] data;
    private int startIndex;

    public ArrayPoint(float time, float... data) {
        this(time, data, 0, data.length);
    }

    public ArrayPoint(float time, float[] data, int startIndex, int dimension) {
        super(dimension, time);
        if (data == null) {
            throw new IllegalArgumentException("The parameter [data] is NULL.");
        }
        if (startIndex < 0 || startIndex >= data.length) {
            throw new IllegalArgumentException("The start index ["+startIndex+"] is out of the range [0, " + (data.length - 1) + "].");
        }
        if (data.length - startIndex < dimension) {
            throw new IllegalArgumentException("The length of piece of the array doesn't correspond to the dimension.");
        }
        this.data = data;
        this.startIndex = startIndex;
    }

    @Override
    public float getValue(int index) {
        if (index < 0 || index >= getDimension()) {
            throw new IllegalArgumentException("The index ["+index+"] is out of the range [0, " + (getDimension() - 1) + "].");
        }
        return data[startIndex + index];
    }
}
