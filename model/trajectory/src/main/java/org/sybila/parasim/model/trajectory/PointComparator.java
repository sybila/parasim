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
 * Enables comparison of two points.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public interface PointComparator {

    /**
     * Returns the number of dimensions of points that can be compared using
     * this comparator.
     *
     * @return Number of dimensions points must have to be comparable using this
     *         comparator.
     */
    int getDimension();

    /**
     * Returns true if the points are similar according to some given criteria
     * specific to the implemented class of PointComparator.
     *
     * @param p1 First point
     * @param p2 Second point
     * @return True if points are similar, false otherwise.
     */
    boolean similar(Point p1, Point p2);
}
