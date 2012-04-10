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

import org.sybila.parasim.model.verification.stl.TimeInterval;
import java.util.List;
import java.util.ArrayList;
import org.sybila.parasim.computation.monitoring.api.PropertyRobustness;
import org.sybila.parasim.model.trajectory.Trajectory;
import java.util.Iterator;

/**
 * Monitors the negation of a subformula. The output is a negation of the signal.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public class NotMonitor<T extends Trajectory>
        implements Evaluable<T, SimplePropertyRobustness> {

    private Evaluable<T, PropertyRobustness> sub;

    public NotMonitor(Evaluable<T, PropertyRobustness> subExpression) {
        if (subExpression == null) {
            throw new NullPointerException("Parameter sub is null.");
        }
        this.sub = subExpression;
    }

    @Override
    public List<SimplePropertyRobustness> evaluate(T trajectory, TimeInterval interval) {
        List<PropertyRobustness> subResult = sub.evaluate(trajectory, interval);
        ArrayList<SimplePropertyRobustness> result = new ArrayList<SimplePropertyRobustness>();
        Iterator<PropertyRobustness> it = subResult.iterator();
        while (it.hasNext()) {
            PropertyRobustness value = it.next();
            result.add(new SimplePropertyRobustness(value.getTime(), -value.value(), -value.getValueDerivative()));
        }
        return result;
    }
}
