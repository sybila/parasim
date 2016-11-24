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
 * General vector of objects in an N-dimensional space.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class GeneralCoordinate<T> {

    private T[] dims;

    /**
     * Create a new vector with a given dimension and components.
     * @param dimension Dimension of underlying integer space.
     * @param coordinate Components.
     * @throws IllegalArgumentException when dimension is invalid (i.e. lesser than or equal to zero).
     */
    public GeneralCoordinate(int dimension, T[] components) {
        if (dimension <= 0) {
            throw new IllegalArgumentException("Dimension not positive: " + dimension);
        }
        dims = Arrays.copyOf(components, dimension);
    }

    /**
     * Return dimension.
     * @return Dimension of this vector.
     */
    public int getDimension() {
        return dims.length;
    }

    /**
     * Return component at given dimension.
     * @param index Dimension.
     * @return Coordinate at designated dimension.
     */
    public T getCoordinate(int index) {
        return dims[index];
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GeneralCoordinate<?>)) {
            return false;
        }
        GeneralCoordinate<?> target = (GeneralCoordinate<?>) obj;
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
     * Builds a vector. Enables to specify components independently.
     */
    public static class Builder<T> {

        private T[] dims;

        /**
         * Create new vector builder. Initially,
         * all components are filled with <code>null</code>
         * @param dimension
         */
        public Builder(int dimension) {
            dims = (T[]) new Object[dimension];
            Arrays.fill(dims, null);
        }

        /**
         * Set component at given dimension.
         * @param index Dimension.
         * @param cmp New value of component at designated dimension.
         * @return This vector builder (with updated coordinate).
         */
        public Builder setCoordinate(int index, T cmp) {
            dims[index] = cmp;
            return this;
        }

        /**
         * Create vector from this builder.
         * Each call returns a new immutable instance,
         * (i.e. it is not backed by this builder).
         * @return New vector with components specified by this builder.
         */
        public GeneralCoordinate create() {
            return new GeneralCoordinate<T>(dims.length, dims);
        }
    }
}
