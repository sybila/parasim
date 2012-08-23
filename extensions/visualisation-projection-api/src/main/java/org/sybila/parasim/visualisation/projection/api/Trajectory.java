package org.sybila.parasim.visualisation.projection.api;

import org.sybila.parasim.model.trajectory.Point;

/**
 * Representation of a trajectory from the visualisation point of view.
 *
 * Contains a sequence of points sorted according to their time values and
 * information about robustness. Should contain at least one point.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface Trajectory extends Iterable<Point> {

    /**
     * Get the robustness associated with trajectory.
     */
    public float getRobustness();

    /**
     * Get the first point of trajectory.
     */
    public Point getInitialPoint();

    /**
     * Get point whose values bound the trajectory.
     */
    public Point getMaximum();

    /**
     * Get a distance of a point from the trajectory.
     */
    public float getDistance(Point target);
}
