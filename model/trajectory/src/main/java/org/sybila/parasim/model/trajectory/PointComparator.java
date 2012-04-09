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
