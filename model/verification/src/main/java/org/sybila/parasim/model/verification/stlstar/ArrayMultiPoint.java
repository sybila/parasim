package org.sybila.parasim.model.verification.stlstar;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.model.trajectory.Point;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ArrayMultiPoint implements MultiPoint {

    private Point[] points;

    private void initialize(Point[] points) {
        Validate.noNullElements(points);
        if (points.length > 0) {
            int dim = points[0].getDimension();
            for (int i = 1; i < points.length; i++) {
                if (points[i].getDimension() != dim) {
                    throw new IllegalArgumentException("Point have different dimensions.");
                }
            }
        }
        this.points = Arrays.copyOf(points, points.length);
    }

    public ArrayMultiPoint(Point[] points) {
        Validate.notNull(points);
        initialize(points);
    }

    public ArrayMultiPoint(List<Point> points) {
        Validate.notNull(points);
        initialize(points.toArray(new Point[0]));
    }

    @Override
    public int getDimension() {
        return points.length;
    }

    @Override
    public Point getPoint(int index) {
        Validate.validIndex(points, index);
        return points[index];
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ArrayMultiPoint)) {
            return false;
        }
        ArrayMultiPoint target = (ArrayMultiPoint) obj;
        return Arrays.equals(points, target.points);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(points);
    }
}
