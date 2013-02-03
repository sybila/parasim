/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.visualisation.plot.impl;

/**
 * Contains points with associated robustness values whose coordinates are
 * projected into 2D.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface Point2DLayer {

    /**
     * Return number of contained points.
     *
     * @return Number of contained points.
     */
    public int size();

    /**
     * Return x coordinate of a given point.
     *
     * @param index Index of a point.
     * @return X coordinate of the point.
     */
    public float getX(int index);

    /**
     * Return y coordinate of a given point.
     *
     * @param index Index of a point.
     * @return Y coordinate of the point.
     */
    public float getY(int index);

    /**
     * Return robustness value associated with a given point.
     *
     * @param index Index of a point.
     * @return Robustness value of the point.
     */
    public float robustness(int index);

    /**
     * Return a lower bound of x coordinates.
     *
     * @return Lower bound of x coordinates.
     */
    public float minX();

    /**
     * Return a lower bound of y coordinates.
     *
     * @return Lower bound of y coordinates.
     */
    public float minY();

    /**
     * Return an upper bound of x coordinates.
     *
     * @return Upper bound of x coordinates.
     */
    public float maxX();

    /**
     * Return an upper bound of y coordinates.
     *
     * @return Upper bound of y coordinates.
     */
    public float maxY();
}
