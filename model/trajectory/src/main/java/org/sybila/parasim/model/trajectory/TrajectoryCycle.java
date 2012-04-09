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
