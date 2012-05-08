package org.sybila.parasim.model.trajectory;

/**
 * Defines Euclidean metric between two points.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class EuclideanMetric implements PointDistanceMetric<Distance> {

    public Distance distance(Point first, Point second) {
        if (first == null) {
            throw new IllegalArgumentException("The first point is null.");
        }
        if (second == null) {
            throw new IllegalArgumentException("The second point is null.");
        }
        if (first.getDimension() != second.getDimension()) {
            throw new IllegalArgumentException("Dimensions of two points should be equal.");
        }
        double sqrDist = 0;
        for (int i = 0; i < first.getDimension(); i++) {
            sqrDist += Math.pow(first.getValue(i) - second.getValue(i), 2);
        }
        return new SimpleDistance((float) Math.sqrt(sqrDist));
    }

    public Distance distance(float[] first, float[] second) {
        if (first == null) {
            throw new IllegalArgumentException("The first point is null.");
        }
        if (second == null) {
            throw new IllegalArgumentException("The second point is null.");
        }
        if (first.length != second.length) {
            throw new IllegalArgumentException("Dimensions of two points should be equal.");
        }
        double sqrDist = 0;
        for (int i = 0; i < first.length; i++) {
            sqrDist += Math.pow(first[i] - second[i], 2);
        }
        return new SimpleDistance((float) Math.sqrt(sqrDist));
    }
}
