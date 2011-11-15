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
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public interface CyclicTrajectoryIterator extends Iterator<Point>
{
    /**
     * Returns true if the next call to next() loops back.
     * @return True if next call to next() will loop back.
     */
    boolean nextLoopsBack();
}
