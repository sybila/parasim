package org.sybila.parasim.visualisation.projection.api;

import org.sybila.parasim.model.trajectory.Point;

/**
 * Grants access to trajectories via their initial point.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface TrajectoryModel {

    /**
     * Get trajectory with given initial point.
     */
    public Trajectory getTrajectory(Point initial);
}
