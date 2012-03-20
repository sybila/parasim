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

    public int getDimension() {
        return dimension;
    }

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
    public float[] toArrayCopy() {
        float[] temp = new float[dimension];
        for (int dim = 0; dim < dimension; dim++) {
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
}
