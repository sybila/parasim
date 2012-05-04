package org.sybila.parasim.model.trajectory;

/**
 * Defines Euclidean metric between two points.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class EuclideanMetric implements DistanceMetric<Point, Distance> {

    public Distance distance(Point first, Point second) {
        if (first.getDimension() != second.getDimension()) {
            throw new IllegalArgumentException("Dimensions of two points should be equal.");
        }
        double sqrDist = 0;
        for (int i = 0; i < first.getDimension(); i++) {
            sqrDist += Math.pow(first.getValue(i) - second.getValue(i), 2);
        }
        return new SimpleDistance((float) Math.sqrt(sqrDist));
    }
}
