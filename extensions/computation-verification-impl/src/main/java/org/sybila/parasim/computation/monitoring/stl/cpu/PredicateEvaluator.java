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
package org.sybila.parasim.computation.monitoring.stl.cpu;

import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.PointDerivative;
import org.sybila.parasim.computation.monitoring.api.PropertyRobustness;

/**
 * Represents a predicate evaluation function which gives the quantitative
 * semantics of a predicate in a point together with it's derivatives.
 *
 * To obtain the point's derivatives either the next point must be given to
 * be able to compute the difference or the point must be extended with derivative
 * values (PointDerivative interface) or the derivatives must be given explicitly.
 *
 * The function is expected to be linear in the coordinates of point p so that
 * it's derivative is a constant.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public interface PredicateEvaluator<R extends PropertyRobustness> {

    R value(Point p, Point next);

    R value(PointDerivative p);
}
