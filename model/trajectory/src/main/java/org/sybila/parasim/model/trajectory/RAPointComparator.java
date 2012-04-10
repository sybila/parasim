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

/**
 * Enables comparing points using <b>Relative</b> and/or <b>Absolute</b> criteria.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public class RAPointComparator implements PointComparator {

    private float[] relTolerance;
    private float[] absTolerance;
    private boolean[] initialized;

    /**
     * Initializes comparator for comparison of points with given dimension.
     * @param dimension
     */
    public RAPointComparator(int dimension) {
        if (dimension <= 0) {
            throw new IllegalArgumentException("dimension must be > 0");
        }
        relTolerance = new float[dimension];
        absTolerance = new float[dimension];
        initialized = new boolean[]{false, false};
    }

    /**
     * Sets the relative tolerance in all dimensions to relTolerance.
     * @param relTolerance value of relative tolerance for all dimensions
     */
    public void setRelTolerance(float relTolerance) {
        if (relTolerance <= 0) {
            throw new IllegalArgumentException("relative tolerance must be positive");
        }
        Arrays.fill(this.relTolerance, relTolerance);
        initialized[0] = true;
    }

    /**
     * Sets the relative tolerance for every dimension.
     * @param relTolerance array of values of relative tolerance for every dimension
     */
    public void setRelTolerance(float[] relTolerance) {
        if (relTolerance == null || relTolerance.length != getDimension()) {
            throw new IllegalArgumentException("relative tolerance array has wrong length");
        }
        for (int i = 0; i < relTolerance.length; i++) {
            if (relTolerance[i] < 0) {
                throw new IllegalArgumentException("relative tolerance must be positive");
            }
        }
        this.relTolerance = relTolerance;
        initialized[0] = true;
    }

    /**
     * Sets the absolute tolerance in all dimensions to absTolerance.
     * @param relTolerance value of absolute tolerance for all dimensions
     */
    public void setAbsTolerance(float absTolerance) {
        if (absTolerance <= 0) {
            throw new IllegalArgumentException("absolute tolerance must be positive");
        }
        Arrays.fill(this.absTolerance, absTolerance);
        initialized[1] = true;
    }

    /**
     * Sets the absolute tolerance for every dimension.
     * @param relTolerance array of values of absolute tolerance for every dimension
     */
    public void setAbsTolerance(float[] absTolerance) {
        if (absTolerance == null || absTolerance.length != getDimension()) {
            throw new IllegalArgumentException("absolute tolerance array has wrong length");
        }
        for (int i = 0; i < absTolerance.length; i++) {
            if (absTolerance[i] < 0) {
                throw new IllegalArgumentException("absolute tolerance must be positive");
            }
        }
        this.absTolerance = absTolerance;
        initialized[1] = true;
    }

    /**
     * Returns the absolute tolerance settings for given dimension.
     * @param index dimension index
     * @return value of absolute tolerance for dimension[index]
     */
    public float getAbsTolerance(int index) {
        if (index < 0 || index >= getDimension()) {
            throw new IllegalArgumentException("index must be in range [0, "
                    + (getDimension() - 1) + ", is " + index);
        }
        return absTolerance[index];
    }

    /**
     * Returns the relative tolerance settings for given dimension.
     * @param index dimension index
     * @return value of relative tolerance for dimension[index]
     */
    public float getRelTolerance(int index) {
        if (index < 0 || index >= getDimension()) {
            throw new IllegalArgumentException("index must be in range [0, "
                    + (getDimension() - 1) + ", is " + index);
        }
        return relTolerance[index];
    }

    /**
     * Returns the dimension for which the comparator is initialized.
     * @return dimension of comparator
     */
    @Override
    public int getDimension() {
        return relTolerance.length;
    }

    /**
     * Compares the two given points in every dimension to the given relative
     * or absolute distance. If the distance is smaller on all dimensions the
     * two points are called <b>similar</b>.
     *
     * At least one dimension has to be initialized for the comparison to be
     * acceptable. If both criterias are setup for a given dimension they
     * must be both fulfilled to make the points similar.
     *
     * @param p1 First point
     * @param p2 Second point
     * @return True if points are similar according to the settings, false else.
     */
    @Override
    public boolean similar(Point p1, Point p2) {
        if (!initialized[0] && !initialized[1]) {
            throw new IllegalArgumentException("Tolerance not initialized, use setAbsTolerance() or setRelTolerance().");
        }
        if (p1.getDimension() != getDimension() || p2.getDimension() != getDimension()) {
            throw new IllegalArgumentException("Dimensions of points must be equal to dimension of comparator.");
        }
        if (initialized[0]) /* Relative tolerance */ {
            float a, b;
            for (int i = 0; i < getDimension(); i++) {
                if (relTolerance[i] == 0) {
                    continue;
                }
                a = p1.getValue(i);
                b = p2.getValue(i);
                if (Math.abs(a - b) / Math.min(Math.abs(a), Math.abs(b)) > relTolerance[i]) {
                    return false;
                }
            }
        }
        if (initialized[1]) /* Absolute tolerance */ {
            for (int i = 0; i < getDimension(); i++) {
                if (absTolerance[i] != 0
                        && Math.abs(p1.getValue(i) - p2.getValue(i)) > absTolerance[i]) {
                    return false;
                }
            }
        }
        return true;
    }
}
