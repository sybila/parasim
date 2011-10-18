package org.sybila.parasim.computation.density;

import org.sybila.parasim.model.trajectory.Point;

/**
 * Defines distance metric between two points. The metric should be symmetric
 * and should return zero distance for two same points.
 * 
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface DistanceMetric {
    
    /**
     * Returns distance between two given points. The method should hold
     * that distance(A, B) == distance(B, A) and distance(A, A) = 0.
     * 
     * @param first
     * @param second
     * @return distance between two given points
     */
    Distance distance(float[] first, float[] second);
    
    /**
     * Returns distance between two given points. The method should hold
     * that distance(A, B) == distance(B, A) and distance(A, A) = 0.
     * 
     * @param first
     * @param second
     * @return distance between two given points
     */
    Distance distance(Point first, Point second);
    
}
