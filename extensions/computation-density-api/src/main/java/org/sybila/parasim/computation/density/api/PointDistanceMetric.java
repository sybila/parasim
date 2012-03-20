package org.sybila.parasim.computation.density.api;

import org.sybila.parasim.model.trajectory.DistanceMetric;
import org.sybila.parasim.model.trajectory.Point;

/**
 * Defines distance metric between two points. The metric should be symmetric
 * and should return zero distance for two same points.
 * 
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface PointDistanceMetric extends DistanceMetric<Point, LimitedDistance> {

    /**
     * Returns distance between two given points. The method should hold
     * that distance(A, B) == distance(B, A) and distance(A, A) = 0.
     * 
     * @param first
     * @param second
     * @return distance between two given points
     */
    LimitedDistance distance(float[] first, float[] second);

    /**
     * Returns distance between two given points. The method should hold
     * that distance(A, B) == distance(B, A) and distance(A, A) = 0.
     * 
     * @param first
     * @param second
     * @return distance between two given points
     */
    @Override
    LimitedDistance distance(Point first, Point second);
}
