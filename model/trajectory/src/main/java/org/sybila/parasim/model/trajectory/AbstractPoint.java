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

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractPoint implements Point {

    private float[] dataInArray;
    private int dimension;
    private float time;

    protected AbstractPoint(int dimension, float time) {
        if (dimension <= 0) {
            throw new IllegalArgumentException("The dimension has to be a positive number.");
        }
        this.dimension = dimension;
        this.time = time;
    }

    @Override
    public int getDimension() {
        return dimension;
    }

    @Override
    public float getTime() {
        return time;
    }

    @Override
    public float[] toArray() {
        if (dataInArray == null) {
            dataInArray = toArrayCopy();
        }
        return dataInArray;
    }

    @Override
    public float[] toArray(int numOfDimensions) {
        return toArrayCopy(numOfDimensions);
    }

    @Override
    public float[] toArrayCopy() {
        return toArrayCopy(getDimension());
    }

    @Override
    public float[] toArrayCopy(int numOfDimensions) {
        if (numOfDimensions > getDimension()) {
            throw new IllegalArgumentException("The number of dimensions has to be at most the same as the dimension of the point. Requested <" + numOfDimensions + ">, dimension <" + getDimension() + ">.");
        }
        float[] temp = new float[numOfDimensions];
        for (int dim = 0; dim < numOfDimensions; dim++) {
            temp[dim] = getValue(dim);
        }
        return temp;
    }

    @Override
    public Iterator<Float> iterator() {
        return new Iterator<Float>() {

            private int index = -1;

            @Override
            public boolean hasNext() {
                return index < getDimension() - 1;
            }

            @Override
            public Float next() {
                index++;
                return getValue(index);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @Override
    public String toString() {
        return time + ":" + Arrays.toString(toArray());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point)) {
            return false;
        }
        Point other = (Point) o;
        if (other.getDimension() != getDimension()) {
            return false;
        }
        if (other.getTime() != getTime()) {
            return false;
        }
        Iterator<Float> thisIter = iterator();
        Iterator<Float> otherIter = other.iterator();
        while (thisIter.hasNext()) {
            if (!thisIter.next().equals(otherIter.next())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Point point, float relativeTolerance) {
        if (point == this) {
            return true;
        }
        if (point.getDimension() != this.getDimension()) {
            return false;
        }
        Iterator<Float> thisIter = iterator();
        Iterator<Float> otherIter = point.iterator();
        while (thisIter.hasNext()) {
            Float thisValue = thisIter.next();
            Float otherValue = otherIter.next();
            float absoluteTolerance = relativeTolerance * Math.min(thisValue, otherValue);
            if (Math.abs(thisValue - otherValue) > absoluteTolerance) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Math.round(getValue(0));
        hash = 97 * hash + Math.round(getTime());
        hash = 97 * hash + getDimension();
        return hash;
    }
}
