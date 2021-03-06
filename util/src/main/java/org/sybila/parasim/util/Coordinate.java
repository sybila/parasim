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

import java.util.Arrays;

/**
 * Vector in an N-dimensional integer space.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class Coordinate {

    private final int[] dims;

    /**
     * Create a new vector with a given dimension and coordinates.
     *
     * @param dimension Dimension of underlying integer space.
     * @param coordinate Coordinates.
     * @throws IllegalArgumentException when dimension is invalid (i.e. lesser
     * than or equal to zero).
     */
    public Coordinate(int dimension, int[] coordinates) {
        if (dimension <= 0) {
            throw new IllegalArgumentException("Dimension not positive: " + dimension);
        }
        dims = Arrays.copyOf(coordinates, dimension);
    }

    /**
     * Return dimension.
     *
     * @return Dimension of this vector.
     */
    public int getDimension() {
        return dims.length;
    }

    /**
     * Return coordinate at given dimension.
     *
     * @param index Dimension.
     * @return Coordinate at designated dimension.
     */
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

    /**
     * Builds a vector. Enables to specify coordinates independently.
     */
    public static class Builder {

        private final int[] dims;

        /**
         * Create new vector builder. Initially, all coordinates are filled with
         * zeros.
         *
         * @param dimension
         */
        public Builder(int dimension) {
            dims = new int[dimension];
            Arrays.fill(dims, 0);
        }

        /**
         * Create new vector builder by copying an existing vector.
         *
         * @param basis Vector to be copied.
         */
        public Builder(Coordinate basis) {
            dims = Arrays.copyOf(basis.dims, basis.getDimension());
        }

        /**
         * Create new vector builder by copying an existing vector with given
         * length.
         *
         * @param basis Vector to be copyied.
         * @param dimension New length of vector.
         */
        public Builder(Coordinate basis, int dimension) {
            dims = Arrays.copyOf(basis.dims, dimension);
        }

        /**
         * Set coordinate at given dimension.
         *
         * @param index Dimension.
         * @param coord New value of coordinate at designated dimension.
         * @return This vector builder (with updated coordinate).
         */
        public Builder setCoordinate(int index, int coord) {
            dims[index] = coord;
            return this;
        }

        /**
         * Create vector from this builder. Each call returns a new immutable
         * instance, (i.e. it is not backed by this builder).
         *
         * @return New vector with coordinates specified by this builder.
         */
        public Coordinate create() {
            return new Coordinate(dims.length, dims);
        }
    }
}
