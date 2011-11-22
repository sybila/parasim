package org.sybila.parasim.model.distance;

/**
 * Defines a distance metric between two objects.
 * The metric should be symmetric
 * and should return zero distance for two same objects.
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public interface DistanceMetric<O extends Object, D extends Distance>
{
    /**
     * Returns distance between two given objects of type O.
     * The method should be symetric ie.
     * distance(A, B) == distance(B, A) and distance(A, A) = 0.
     *
     * @param first
     * @param second
     * @return distance between two given points
     */
    D distance(O first, O second);

}
