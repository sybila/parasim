package org.sybila.parasim.extension.visualisation.projection.model.points;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.util.Pair;
import org.sybila.parasim.visualisation.projection.api.points.Point2D;
import org.sybila.parasim.visualisation.projection.api.points.SingleLayer;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class MapSingleLayer implements SingleLayer {

    private Map<Point2D, Pair<Point, Robustness>> points;
    private Point2D min, max;

    public MapSingleLayer(Map<Point2D, Pair<Point, Robustness>> points, Point2D minBounds, Point2D maxBounds) {
        if (points == null) {
            throw new IllegalArgumentException("Null map not allowed.");
        }
        if (minBounds == null) {
            throw new IllegalArgumentException("Null minimum bounds not allowed.");
        }
        if (maxBounds == null) {
            throw new IllegalArgumentException("Null maximum bounds not allowed.");
        }
        this.points = Collections.unmodifiableMap(points);
        min = minBounds;
        max = maxBounds;
    }

    @Override
    public Iterator<Point2D> iterator() {
        return points.keySet().iterator();
    }

    @Override
    public Point getPoint(Point2D target) {
        Pair<Point, Robustness> result = points.get(target);
        if (result == null) {
            return null;
        }
        return result.first();
    }

    @Override
    public Robustness getRobustness(Point2D target) {
        Pair<Point, Robustness> result = points.get(target);
        if (result == null) {
            return null;
        }
        return result.second();
    }

    @Override
    public float getMaxX() {
        return max.getX();
    }

    @Override
    public float getMaxY() {
        return max.getY();
    }

    @Override
    public float getMinX() {
        return min.getX();
    }

    @Override
    public float getMinY() {
        return min.getY();
    }
}
