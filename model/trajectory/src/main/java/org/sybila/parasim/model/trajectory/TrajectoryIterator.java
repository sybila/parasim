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

import java.util.Iterator;

/**
 * Enables iterating over points of a trajectory with arbitrary jumps.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public interface TrajectoryIterator extends Iterator<Point> {

    /**
     * If jump = 1 then the behaviour is equal to hasNext().
     *
     * @param jump Number of next elements that must exist to return true.
     * @return True if there exists at least jump more elements, false else.
     */
    boolean hasNext(int jump);

    /**
     * If jump = 1 then the behaviour is equal to next().
     *
     * @param jump Number of times next() would have to be called to return
     *        the same element.
     * @return Point if there exists at least jump more elements, null else.
     */
    Point next(int jump);

    /**
     * Returns the position of the point to which the iterator is currently
     * pointing. This means that:
     *
     * it.next() == trajectory.getPoint(it.getPositionOnTrajectory())
     *
     * The first point on a untruncated trajectory is expected to have
     * position == 0.
     *
     * If hasNext() == false then an exception is thrown.
     *
     * @return Unique position on trajectory.
     */
    int getPositionOnTrajectory();
}
