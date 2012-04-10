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
package org.sybila.parasim.computation.monitoring.api;

import org.sybila.parasim.model.distance.Distance;

/**
 * Represents the robustness of a property on a single trajectory of an OdeSystem.
 *
 * It is a number in some time moment stating how much another trajectory's
 * secondary signal may deviate from this one's and still have the same property
 * validity.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public interface PropertyRobustness extends Distance {
    /**
     * Returns the time for which the robustness was computed.
     * @return time
     */
    float getTime();

    /**
     * Returns the derivative of the value in given point. This is due to the
     * fact that a trajectory's robustness may not be continuous from the left
     * and thus to compute a value in between two neighbouring time points
     * the value's derivatives are necessary.
     *
     * @return derivate of value in the time where it was computed
     */
    float getValueDerivative();
}
