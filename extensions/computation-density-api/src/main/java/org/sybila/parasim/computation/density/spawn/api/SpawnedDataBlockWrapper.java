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

import java.util.Iterator;

import org.sybila.parasim.computation.density.api.Configuration;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.trajectory.TrajectoryWithNeighborhood;

public class SpawnedDataBlockWrapper implements SpawnedDataBlock {

    private Configuration configuration;
    private DataBlock<Trajectory> secondaryTrajectories;
    private DataBlock<TrajectoryWithNeighborhood> trajectories;

    public SpawnedDataBlockWrapper(DataBlock<TrajectoryWithNeighborhood> trajectories, Configuration configuration, DataBlock<Trajectory> secondaryTrajectories) {
        if (trajectories == null) {
            throw new IllegalArgumentException("The parameter trajectories is null.");
        }
        if (configuration == null) {
            throw new IllegalArgumentException("The parameter configurationis null.");
        }
        if (secondaryTrajectories == null) {
            throw new IllegalArgumentException("The parameter secondaryTrajectories is null.");
        }
        this.trajectories = trajectories;
        this.configuration = configuration;
        this.secondaryTrajectories = secondaryTrajectories;
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public TrajectoryWithNeighborhood getTrajectory(int index) {
        return trajectories.getTrajectory(index);
    }

    @Override
    public DataBlock<Trajectory> getSecondaryTrajectories() {
        return secondaryTrajectories;
    }

    @Override
    public int size() {
        return trajectories.size();
    }

    @Override
    public Iterator<TrajectoryWithNeighborhood> iterator() {
        return trajectories.iterator();
    }
}
