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
package org.sybila.parasim.model.math;

import java.util.Collection;
import org.sybila.parasim.model.trajectory.Point;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class Power extends BinaryOperator<Power> {

    public Power(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    protected BinaryOperator create(Expression left, Expression right) {
        return new Power(left, right);
    }

    @Override
    public float evaluate(Point point) {
        return (float) Math.pow(getLeft().evaluate(point), getRight().evaluate(point));
    }

    @Override
    public float evaluate(float[] point) {
        return (float) Math.pow(getLeft().evaluate(point), getRight().evaluate(point));
    }

    @Override
    public Power release(Expression... expressions) {
        return new Power(getLeft().release(expressions), getRight().release(expressions));
    }

    @Override
    public Power release(Collection<Expression> expressions) {
        return new Power(getLeft().release(expressions), getRight().release(expressions));
    }

    @Override
    public Power substitute(SubstitutionValue... substitutionValues) {
        return new Power(getLeft().substitute(substitutionValues), getRight().substitute(substitutionValues));
    }

    @Override
    public Power substitute(Collection<SubstitutionValue> substitutionValues) {
        return new Power(getLeft().substitute(substitutionValues), getRight().substitute(substitutionValues));
    }

    @Override
    public String toFormula() {
        return "(" + getLeft().toFormula() + ") ^ (" + getRight().toFormula() + ")";
    }

    @Override
    public String toFormula(VariableRenderer renderer) {
        return "(" + getLeft().toFormula(renderer) + ") ^ (" + getRight().toFormula(renderer) + ")";
    }



}
