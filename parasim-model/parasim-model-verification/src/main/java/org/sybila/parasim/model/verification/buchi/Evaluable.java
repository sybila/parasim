package org.sybila.parasim.model.verification.buchi;
import org.sybila.parasim.model.trajectory.Point;

/**
 * Enables the evaluation of expressions over a single point.
 *
 * @author Sven Dražan <sven@mail.muni.cz>
 */
public interface Evaluable<P extends Point> {
    /**
     * Evaluates the degree of satisfaction of an expression in a given point.
     *
     * @param point point in which to evaluate expression
     * @return degree of satisfaction (>0 = satisfied, <0 = unsatisfied)
     */
    public float evaluate(P point);

    /**
     * Evaluates boolean validity of an expression in a given point.
     * @param point point in which to evaluate expression
     * @return validity of expression
     */
    public boolean valid(P point);

    @Override
    public String toString();
}
