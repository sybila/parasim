/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
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
import java.util.List;
import org.sybila.parasim.model.trajectory.Point;

public class Function implements Expression<Function> {

    private final String name;
    private final Expression body;
    private final List<Parameter> arguments;

    public Function(String name, Expression body, List<Parameter> arguments) {
        this.name = name;
        this.body = body;
        this.arguments = arguments;
    }

    public String getName() {
        return name;
    }

    public List<Parameter> getArguments() {
        return arguments;
    }

    public Expression getBody() {
        return body;
    }

    @Override
    public float evaluate(Point point) {
        return body.evaluate(point);
    }

    @Override
    public float evaluate(float[] point) {
        return body.evaluate(point);
    }

    @Override
    public Function release(Expression... expressions) {
        return new Function(name, body.release(expressions), arguments);
    }

    @Override
    public Function release(Collection<Expression> expressions) {
        return new Function(name, body.release(expressions), arguments);
    }

    @Override
    public Function substitute(SubstitutionValue... substitutionValues) {
        return new Function(name, body.substitute(substitutionValues), arguments);
    }

    @Override
    public Function substitute(Collection<SubstitutionValue> substitutionValues) {
        return new Function(name, body.substitute(substitutionValues), arguments);
    }

    @Override
    public <T> T traverse(TraverseFunction<T> function) {
        return function.apply(this, (T) body.traverse(function));
    }

    @Override
    public String toFormula() {
        return body.toFormula();
    }

    @Override
    public String toFormula(VariableRenderer renderer) {
        return body.toFormula(renderer);
    }

    @Override
    public StringBuilder toFormula(StringBuilder builder) {
        return body.toFormula(builder);
    }

    @Override
    public StringBuilder toFormula(StringBuilder builder, VariableRenderer renderer) {
        return body.toFormula(builder, renderer);
    }

}
