package org.sybila.parasim.visualisation.projection.api.points;

import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.verification.Robustness;

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

    public Robustness getRobustness(Point2D target);
}
