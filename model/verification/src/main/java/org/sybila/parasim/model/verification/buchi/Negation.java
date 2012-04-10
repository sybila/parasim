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
