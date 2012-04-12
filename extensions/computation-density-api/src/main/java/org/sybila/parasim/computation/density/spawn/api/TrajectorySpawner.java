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
package org.sybila.parasim.computation.density.spawn.api;

import org.sybila.parasim.computation.density.api.Configuration;
import org.sybila.parasim.computation.density.api.InitialSampling;
import org.sybila.parasim.computation.density.distancecheck.api.DistanceCheckedDataBlock;
import org.sybila.parasim.model.space.OrthogonalSpace;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface TrajectorySpawner {

    /**
     * Creates new trajectories.
     * WARNING: It updates neighborhood defined in configuration.
     * @param configuration
     * @param trajectories
     * @return data block with new trajectories
     */
    SpawnedDataBlock spawn(Configuration configuration, DistanceCheckedDataBlock trajectories);

    /**
     * Creates new trajectories in the given space. Number of trajectories is defined
     * through the numOfSamples parameter.
     *
     * @param  configuration
     * @param space
     * @param numOfSamples
     */
    SpawnedDataBlock spawn(OrthogonalSpace space, InitialSampling initialSampling);
}
