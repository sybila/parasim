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
 * Enables the evaluation of expressions over a single point.
 *
 * @author Sven Dra�an <sven@mail.muni.cz>
 */
public interface Evaluable<P extends Point> {
    /**
     * Evaluates the degree of satisfaction of an expression in a given point.
     *
     * @param point point in which to evaluate expression
     * @return degree of satisfaction (>0 = satisfied, <0 = unsatisfied)
     */
    public float evaluate(P point);

    /**
     * Evaluates boolean validity of an expression in a given point.
     * @param point point in which to evaluate expression
     * @return validity of expression
     */
    public boolean valid(P point);

    @Override
    public String toString();
}
