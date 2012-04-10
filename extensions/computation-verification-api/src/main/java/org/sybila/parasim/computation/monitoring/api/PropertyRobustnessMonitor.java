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

import org.sybila.parasim.model.verification.Property;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * Represents a method to compute the robustness of a property on a trajectory
 * of an OdeSystem.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public interface PropertyRobustnessMonitor<P extends Property, T extends Trajectory, R extends PropertyRobustness> {
    /**
     * Computes the robustness of the property on the trajectory.
     *
     * @param property Property who's robustness to compute on trajectory.
     * @param trajectory Trajectory on which to evaluate robustness of property.
     * @return Robustness of property on trajectory.
     */
    R computeRobustness(P property, T trajectory);

}
