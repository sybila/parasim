package org.sybila.parasim.visualisation.projection.api;

import java.util.Collection;
import org.sybila.parasim.model.trajectory.Point;

/**
 * General construct which displays trajectories associated with given initial
 * conditions (starting points). Displays either a single trajectory, or several
 * trajectories in neighbourhood with one being central (may be drawn
 * differently according to distance from "central" trajectory").
 *
 * As the robustness display does not have to possess information about
 * trajectories, they are passed by the means of starting points.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface TrajectoryDisplay {

    /**
     * Clear all displayed trajectories and display trajectory going out of a
     * single point.
     *
     * @param target Starting point of displayed trajectory.
     */
    public void displayPoint(Point target);

    /**
     * Clear all displayed trajectories and display a trajectory and its
     * neighbourhood.
     *
     * @param center Central trajectory.
     * @param neighbourhood Neighbouring trajectories.
     */
    public void displayPoints(Point center, Collection<Point> neighbourhood);

    public void clear();
}
