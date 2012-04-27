package org.sybila.parasim.visualisation.plot.impl.layer.utils;

import java.util.Arrays;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class Coordinate {

    private int[] dims;

    public Coordinate(int dimension, int[] coordinates) {
        dims = Arrays.copyOf(coordinates, dimension);
    }

    public int getDimension() {
        return dims.length;
    }

    public int getCoordinate(int index) {
        return dims[index];
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Coordinate)) {
            return false;
        }
        Coordinate target = (Coordinate) obj;
        return Arrays.equals(dims, target.dims);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(dims);
    }

    @Override
    public String toString() {
        return Arrays.toString(dims);
    }

    public static class Builder {

        private int[] dims;

        public Builder(int dimension) {
            dims = new int[dimension];
            Arrays.fill(dims, 0);
        }

        public Builder setCoordinate(int index, int coord) {
            dims[index] = coord;
            return this;
        }

        public Coordinate create() {
            return new Coordinate(dims.length, dims);
        }
    }
}
