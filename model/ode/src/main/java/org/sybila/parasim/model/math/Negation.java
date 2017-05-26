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
public class Negation implements Expression<Negation> {

    private final Expression expression;

    public Negation(Expression expression) {
        if (expression == null) {
            throw new IllegalArgumentException("The parameter [negation] is null.");
        }
        this.expression = expression;
    }

    public Expression getSubExpression() {
        return expression;
    }

    @Override
    public float evaluate(Point point) {
        return - expression.evaluate(point);
    }

    @Override
    public float evaluate(float[] point) {
        return - expression.evaluate(point);
    }

    @Override
    public Negation release(Expression... expressions) {
        return new Negation(expression.release(expressions));
    }

    @Override
    public Negation release(Collection<Expression> expressions) {
        return new Negation(expression.release(expressions));
    }

    @Override
    public Negation substitute(SubstitutionValue... substitutionValues) {
        return new Negation(expression.substitute(substitutionValues));
    }

    @Override
    public Negation substitute(Collection<SubstitutionValue> substitutionValues) {
        return new Negation(expression.substitute(substitutionValues));
    }

    @Override
    public String toFormula() {
        return toFormula(new StringBuilder()).toString();
    }

    @Override
    public String toFormula(VariableRenderer renderer) {
        return toFormula(new StringBuilder(), renderer).toString();
    }

    @Override
    public StringBuilder toFormula(StringBuilder builder) {
        return toFormula(builder, VariableRenderer.NAME);
    }

    @Override
    public StringBuilder toFormula(StringBuilder builder, VariableRenderer renderer) {
        builder.append("-(");
        return expression.toFormula(builder, renderer).append(")");
    }

    @Override
    public <T> T traverse(TraverseFunction<T> function) {
        return function.apply(this);
    }

}
