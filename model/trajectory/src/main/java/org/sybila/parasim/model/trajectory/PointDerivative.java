package org.sybila.parasim.model.trajectory;

/**
 * Enables retrieval of the system's derivative in the given point.
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public interface PointDerivative extends Point
{
    float getDerivative(int index);
}
