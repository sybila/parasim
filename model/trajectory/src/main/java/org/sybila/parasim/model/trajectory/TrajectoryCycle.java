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
package org.sybila.parasim.model.trajectory;

/**
 * Enables information retrieval about a possible cycle on a trajectory.
 *
 * If the trajectory contains a cycle then hasCycle() returns true and all
 * other methods of this interface are well defined, if not then hasCycle()
 * returns false and all the methods throw a RuntimeException.
 *
 * A cycle is marked by two points (xS - start, xE - end) which have been found
 * similar by some PointComparator.
 *
 * x0, x1, x2, ..., xS, x(S+1), x(S+2), ..., xE, x(E+1), x(E+2), ...
 *                  ^                        ^
 * These points and the ones in between are considered
 * as a cycle and it can be expected that if more points on the trajectory would
 * be simulated they would be also similar or close to being similar, ie.
 * xS ~ xE, x(S+1) ~ x(E+1), x(S+2) ~ x(E+2), ...
 *
 * Because of this the points after xE can be truncated and xE can be the last
 * point of the trajectory however it needn't be.
 *
 * The positions of xS and xE are returned by getCycleStartPosition() and
 * getCycleEndPosition().
 *
 * Possible infinite iteration is enabled by the CyclicTrajectoryIterator
 * given by the cyclicIterator() methods.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public interface TrajectoryCycle {
    /**
     * @return True if the trajectory has a cycle, false otherwise.
     */
    boolean hasCycle();

    /**
     * @return Position of the start of the cycle.
     */
    int getCycleStartPosition();

    /**
     * @return Position of the end of the cycle.
     */
    int getCycleEndPosition();

    CyclicTrajectoryIterator cyclicIterator();

    CyclicTrajectoryIterator cyclicIterator(int index);
}
