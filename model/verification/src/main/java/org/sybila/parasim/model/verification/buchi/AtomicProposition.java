/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
