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

/**
 * Evaluates simple inequality predicates in given points.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public class InequalityEvaluator implements PredicateEvaluator<SimplePropertyRobustness> {

    private int dimIndex;
    private float constant;
    private InequalityType op;

    InequalityEvaluator(int dimIndex, float constant, InequalityType op) {
        if (dimIndex < 0) {
            throw new IllegalArgumentException("Parameter dimIndex must be >= 0.");
        }
        this.constant = constant;
        this.dimIndex = dimIndex;
        this.op = op;
    }

    @Override
    public SimplePropertyRobustness value(Point p, Point next) {
        return new SimplePropertyRobustness(p.getTime(),
                op.value(p.getValue(dimIndex), constant),
                op.derivative((next.getValue(dimIndex) - p.getValue(dimIndex)) / (next.getTime() - p.getTime())));
    }

    @Override
    public String toString() {
        return "(X[" + dimIndex + "] " + op.toString() + " " + constant + ")";
    }

    @Override
    public SimplePropertyRobustness value(PointDerivative p) {
        return new SimplePropertyRobustness(p.getTime(),
                op.value(p.getValue(dimIndex), constant),
                op.derivative(p.getDerivative(dimIndex)));
    }
}
