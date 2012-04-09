package org.sybila.parasim.model.verification.buchi;

import org.sybila.parasim.model.trajectory.Point;

/**
 * Atomic proposition as the comparison of two operands.
 * @author Sven Drazan <sven@mail.muni.cz>
 */
public class AtomicProposition<P extends Point> implements Evaluable<P> {

    private AtomicPropOperand op1;
    private AtomicPropOperand op2;
    private AtomicPropOperator operator;

    /**
     * Constructor
     *
     * @param op1 first operand
     * @param op operator to apply
     * @param op2 second operator
     */
    public AtomicProposition(AtomicPropOperand op1, AtomicPropOperator operator, AtomicPropOperand op2) {
        this.op1 = op1;
        this.op2 = op2;
        this.operator = operator;
    }

    /**
     * Returns degree of satisfaction of the proposition in given state.
     * @param point Point in which to evaluate the atomic proposition.
     * @return Degree of satisfaction of the proposition in given point.
     */
    public float evaluate(P point) {
        return operator.evaluate(op1.getValue(point), op2.getValue(point));
    }

    /**
     * Returns validity of the proposition in given state.
     * @param point Point in which to validate the atomic proposition.
     * @return Validity of the proposition in given point.
     */
    public boolean valid(P point) {
        return operator.validate(op1.getValue(point), op2.getValue(point));
    }

    @Override
    public String toString() {
        return op1.toString() + " " + operator.toString() + " " + op2.toString();
    }
}
