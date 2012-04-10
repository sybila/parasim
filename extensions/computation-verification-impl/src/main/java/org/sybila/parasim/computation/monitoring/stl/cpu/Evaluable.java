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
import org.sybila.parasim.computation.monitoring.api.PropertyRobustness;
import org.sybila.parasim.model.trajectory.Trajectory;
import java.util.List;

/**
 * Enables the evaluation of a property's robustness on a given trajectory.
 * For computational purposes the trajectory may be looked at as prolonged past
 * it's last point using the last point's value and valueDerivative.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 * @param <T> Class of trajectories on which property robustness computation
 *            is enabled.
 * @param <R> Class of property robustness that will be computed.
 */
public interface Evaluable<T extends Trajectory, R extends PropertyRobustness> {

    /**
     * Evaluates the property's robustness on the given trajectory over a segment
     * covering the given time interval.
     *
     * If the trajectory is null or empty an exception is thrown.
     *
     * If the trajectory is shorter then the required interval it is prolonged
     * using it's last point and it's value's derivative.
     *
     * @param trajectory Trajectory on which to evaluate robustness.
     * @param interval Time interval over which to evaluate robustness.
     * @return List of time points with computed robustness values and their
     *         derivatives. The time of the first point will be
     *         precisely <code>interval.getLowerBound()</code>, the
     *         time of the last point will be <code>interval.getUpperBound()</code>
     *         or less depending on the length of the trajectory.
     */
    List<R> evaluate(T trajectory, TimeInterval interval);
}
