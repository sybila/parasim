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
package org.sybila.parasim.model.ode;

import org.sybila.parasim.model.math.Expression;
import org.sybila.parasim.model.math.Variable;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class OdeSystemVariable extends Variable {

    private final Expression rightSideExpression;

    public OdeSystemVariable(String name, int index, Expression rightSideExpression) {
        super(name, index);
        if (rightSideExpression == null) {
            throw new IllegalArgumentException("The parameter [rightSideExpression] is null.");
        }
        this.rightSideExpression = rightSideExpression;
    }

    public OdeSystemVariable(Variable variable, Expression rightSideExpression) {
        this(variable.getName(), variable.getIndex(), rightSideExpression);
    }

    public final Expression getRightSideExpression() {
        return rightSideExpression;
    }

}
