package org.sybila.parasim.model.trajectory;

/**
 * Defines distance metric between two points. The metric should be symmetric
 * and should return zero distance for two same points.
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface PointDistanceMetric<D extends Distance> extends DistanceMetric<Point, D> {

    /**
     * Returns distance between two given points. The method should hold
     * that distance(A, B) == distance(B, A) and distance(A, A) = 0.
     *
     * @param first
     * @param second
     * @return distance between two given points
     */
    D distance(float[] first, float[] second);

}
