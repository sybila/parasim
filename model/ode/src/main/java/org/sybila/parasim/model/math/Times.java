/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
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
public final class Times extends BinaryOperator<Times> {

    public Times(Expression left, Expression right) {
        super(left, right);
    }

    public Times(Expression... expressions) {
        super(expressions);
    }

    @Override
    public float evaluate(Point point) {
        return getLeft().evaluate(point) * getRight().evaluate(point);
    }

    @Override
    public float evaluate(float[] point) {
        return getLeft().evaluate(point) * getRight().evaluate(point);
    }

    @Override
    public Times release(Expression... expressions) {
        return new Times(getLeft().release(expressions), getRight().release(expressions));
    }

    @Override
    public Times release(Collection<Expression> expressions) {
        return new Times(getLeft().release(expressions), getRight().release(expressions));
    }

    @Override
    public Times substitute(SubstitutionValue... substitutionValues) {
        return new Times(getLeft().substitute(substitutionValues), getRight().substitute(substitutionValues));
    }

    @Override
    public Times substitute(Collection<SubstitutionValue> substitutionValues) {
        return new Times(getLeft().substitute(substitutionValues), getRight().substitute(substitutionValues));
    }

    @Override
    protected BinaryOperator create(Expression left, Expression right) {
        return new Times(left, right);
    }

    @Override
    protected int getPriority() {
        return 10;
    }

    @Override
    protected String getSymbol() {
        return "*";
    }

}
