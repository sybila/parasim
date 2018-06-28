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

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public final class ParameterValue implements SubstitutionValue<Parameter> {

    private final Parameter parameter;
    Expression substitution;

    public ParameterValue(Parameter paramater, Expression substitution) {
        if (paramater == null) {
            throw new IllegalArgumentException("The paramater [parameter] is null.");
        }
        if (substitution == null) {
            throw new IllegalArgumentException("The paramater [substitution] is null.");
        }
        this.parameter = paramater;
        this.substitution = substitution;;
    }

    public ParameterValue(Parameter parameter, float value) {
        this(parameter, new Constant(value));
    }

    @Override
    public final Parameter getExpression() {
        return parameter;
    }

    @Override
    public Expression getSubstitution() {
        return substitution;
    }

    @Override
    public String toString() {
        return parameter.getName() + ": " + substitution.toString();
    }

    public void setSubstitution(Expression substitution) {
        this.substitution = substitution;
    }
}
