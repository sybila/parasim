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
package org.sybila.parasim.computation.density.api;

import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.trajectory.LimitedPointDistanceMetric;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.trajectory.TrajectoryNeighborhood;

/**
 * Object containing configuration information.
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Configuration {

    /**
     * @return metric used to determine whether two trajectories has valid/invalid distance
     */
    LimitedPointDistanceMetric getDistanceMetric();

    /**
     * @return information how the initial space is sampled
     */
    InitialSampling getInitialSampling();

    /**
     * @return initial space
     */
    OrthogonalSpace getInitialSpace();

    TrajectoryNeighborhood<Trajectory> getNeighborhood();

    int getStartIndex(int index, int neighborIndex);
}
