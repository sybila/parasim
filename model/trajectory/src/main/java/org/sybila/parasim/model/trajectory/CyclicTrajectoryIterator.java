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

import java.util.Iterator;

/**
 * Enables iteration over a trajectory with a possible cycle.
 *
 * A cycle is marked by two points (xS - start, xE - end) that have been found
 * similar by some PointComparator.
 *
 * x0, x1, x2, ..., xS, x(S+1), x(S+2), ..., xE, x(E+1), x(E+2), ...
 *                  ^                        ^
 * If indeed there is a cycle the iterator will go through the cycle
 * forever looping back over and over again.
 *
 * The point of loopback is indicated by the nextLoopsBack() function which
 * return's true if the next call to next would return xE however xS' will
 * be returned instead.
 *
 * xS'.getValue(i) == xS.getValue(i) but
 * xS'.getTime() == xS.getTime + (xE.getTime() - xS.getTime())
 *
 * The apostrophes in xS' indicate that once the iterator iterates
 * through the loopback the returned points have same dimensional values as
 * xS,x(S+1).. but have a shifted time value by the period of the cycle.
 * This is described by definition:
 *
 * L = E-S                                          (length of cycle)
 * T = xE.getTime() - xS.getTime()                  (period of cycle)
 * \forall k \in N such that k > S && k = S+i*L+r
 * xk.getValue(m) == x(S+r).getValue(m)
 * xk.getTime() == x(S+r).getTime()+i*T
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public interface CyclicTrajectoryIterator extends Iterator<Point> {

    /**
     * Returns true if the next call to next() loops back.
     * @return True if next call to next() will loop back.
     */
    boolean nextLoopsBack();
}
