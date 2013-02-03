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

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public final class VariableValue implements SubstitutionValue<Variable> {

    private final Variable variable;
    private final Expression substitution;

    public VariableValue(Variable variable, Expression substitution) {
        if (variable == null) {
            throw new IllegalArgumentException("The paramater [variable] is null.");
        }
        this.variable = variable;
        this.substitution = substitution;
    }

    public VariableValue(Variable variable, float value) {
        this(variable, new Constant(value));
    }

    @Override
    public final Variable getExpression() {
        return variable;
    }

    @Override
    public Expression getSubstitution() {
        return substitution;
    }

    @Override
    public String toString() {
        return "[" + getExpression().getName() + "/" + getSubstitution().toString() + "]";
    }

}
