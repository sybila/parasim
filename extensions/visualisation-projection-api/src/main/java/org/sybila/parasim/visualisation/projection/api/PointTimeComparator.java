package org.sybila.parasim.visualisation.projection.api;

import java.util.Comparator;
import org.sybila.parasim.model.trajectory.Point;

/**
 * Compares two points according to their time. Enforces same dimension.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public enum PointTimeComparator implements Comparator<Point> {

    INSTANCE;

    public static PointTimeComparator getInstance() {
        return INSTANCE;
    }

    @Override
    public int compare(Point t1, Point t2) {
        if (t1.getDimension() != t2.getDimension()) {
            throw new IllegalArgumentException("The points have different dimensions and are not comparable.");
        }
        return Float.compare(t1.getTime(), t2.getTime());
    }
}
