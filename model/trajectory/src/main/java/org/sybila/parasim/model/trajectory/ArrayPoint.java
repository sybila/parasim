package org.sybila.parasim.model.trajectory;

import java.util.Iterator;

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
            throw new IllegalArgumentException("The start index is out of the range [0, " + (data.length - 1) + "].");
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
            throw new IllegalArgumentException("The index is out of the range [0, " + (getDimension() - 1) + "].");
        }
        return data[startIndex + index];
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
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Math.round(getValue(0));
        hash = 97 * hash + Math.round(getTime());
        hash = 97 * hash + getDimension();
        return hash;
    }
}
