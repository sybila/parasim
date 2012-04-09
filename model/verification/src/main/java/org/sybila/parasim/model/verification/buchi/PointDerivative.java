package org.sybila.parasim.model.verification.buchi;

import org.sybila.parasim.model.trajectory.Point;

/**
 * Extends the Point interface to return also the derivative.
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public interface PointDerivative extends Point {
    float getDerivative(int varIndex);
}
