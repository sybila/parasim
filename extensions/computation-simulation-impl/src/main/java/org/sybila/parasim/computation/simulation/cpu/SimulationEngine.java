/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.computation.simulation.cpu;

import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface SimulationEngine {

    void close();

    /**
     * Performs simulation of a differential equation system from given point in time to timelimit
     * @param point where to start simulation, contains start time
     * @param odeSystem differential equation system
     * @param timeLimit end time of simulation
     * @param configuration relative error, absolute error and time step configuration
     * @return computed trajectory
     */
    Trajectory simulate(Point point, OdeSystem odeSystem, double timeLimit, PrecisionConfiguration configuration);

}
