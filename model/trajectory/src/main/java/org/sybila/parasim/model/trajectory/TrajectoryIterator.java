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
