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
package org.sybila.parasim.computation.simulation.api;

import java.io.Serializable;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.space.OrthogonalSpace;

/**
 * The simulation descriptor
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Configuration extends Serializable {

    /**
     * Returns number of dimensions of points on all trajectories to be
     * simulated.
     *
     * @return number of dimensions
     */
    int getDimension();

    /**
     * Returns space containing upper and lower bounds on all space-time dimensions.
     * The simulation of trajectory stops if the space border is reached.
     *
     * @return orthogonal space
     */
    OrthogonalSpace getSpace();

    /**
     * Returns the amount of work to do inside the simulate method
     * during the simulation of each trajectory
     *
     * @return amount of work to do inside the simulate method
     */
    int getMaxNumberOfIterations();

    /**
     * Returns ODE system which is used for the simulation
     *
     * @return ODE system
     */
    OdeSystem getOdeSystem();

    /**
     * Returns the maximum distance of two consecutive points
     * of one trajectory in all dimensions. If some dimension is not
     * to be checked the distance is set to 0.
     *
     * @return maximum distance of two consecutive points
     */
    float[] getSteps();
}