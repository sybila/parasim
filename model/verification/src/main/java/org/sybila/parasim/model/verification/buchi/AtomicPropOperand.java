package org.sybila.parasim.model.verification.buchi;

import org.sybila.parasim.model.trajectory.Point;

/**
 * Interface representing an abstract operand in an atomic proposition.
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public interface AtomicPropOperand<P extends Point> {
    /**
     * Returns the value of the operand in given point
     * @param point in which to evaluate operand
     * @return value of operand in point
     */
    float getValue(P point);

    @Override
    String toString();
}
