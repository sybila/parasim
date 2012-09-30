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

import java.io.Serializable;

/**
 * Stores points of one trajectory.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Trajectory extends Iterable<Point>, Serializable {

    /**
     * Returns number of dimensions of each point of the trajectory
     *
     * @return Number of dimensions of each point of the trajectory.
     */
    int getDimension();

    /**
     * Returns the first point of the trajectory to be yet computed.
     *
     * @return The first point of the trajectory to be yet computed.
     */
    Point getFirstPoint();

    /**
     * Returns the last point of the trajectory to be yet computed.
     *
     * @return The last point of the trajectory to be yet computed.
     */
    Point getLastPoint();

    /**
     * Returns the total number of points of the trajectory yet computed.
     *
     * @return The total number of points of the trajectory yet computed.
     */
    int getLength();

    /**
     * Returns the point with specified position from this trajectory.
     *
     * @param index position on trajectory
     * @return the point with the specified position
     */
    Point getPoint(int index);

    /**
     * Returns the reference pointed to this trajectory
     *
     * @return reference pointed to this trajectory
     */
    TrajectoryReference getReference();

    /**
     * Checks whether the trajectory contains the point on the specified
     * position.
     *
     * @param index
     * @return true if trajectory contains point with such index
     */
    boolean hasPoint(int index);

    /**
     * Iterates over points of the trajectory starting from the first point.
     * Complexity of next() is constant.
     *
     * @return Iterator with next() pointing to the first point of the trajectory.
     */
    TrajectoryIterator iterator();

    /**
     * Iterates over points of the trajectory starting from point with given index.
     * Complexity of next() is constant.
     *
     * @param index Starting point of iteration.
     * @return Iterator with next() pointing to the point with specified index
     *         on success.
     */
    TrajectoryIterator iterator(int index);
}
