package org.sybila.parasim.visualisation.projection.api;

import org.sybila.parasim.model.trajectory.Point;

/**
 * Deals with events that select a point.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface PointSelectionListener {

    /**
     * Sent when a point is selected.
     *
     * @param target Selected point.
     */
    public void pointSelected(Point target);
}
