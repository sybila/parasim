package org.sybila.parasim.visualisation.projection.api.points;

import org.sybila.parasim.model.trajectory.Point;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface SingleLayer extends Iterable<Point2D> {

    public float getMinX();

    public float getMaxX();

    public float getMinY();

    public float getMaxY();

    public Point getPoint(Point2D target);
}
