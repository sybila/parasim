package org.sybila.parasim.model.verification.buchi;

import org.sybila.parasim.model.trajectory.Point;

/**
 * Negation of an expression.
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public class Negation<P extends Point> implements Evaluable<P> {

    private Evaluable subExpression;

    public Negation(Evaluable subExpression) {
        this.subExpression = subExpression;
    }

    public float evaluate(P point) {
        return -subExpression.evaluate(point);
    }

    public boolean valid(P point) {
        return !subExpression.valid(point);
    }

    @Override
    public String toString() {
        return "!" + subExpression.toString();
    }
}
