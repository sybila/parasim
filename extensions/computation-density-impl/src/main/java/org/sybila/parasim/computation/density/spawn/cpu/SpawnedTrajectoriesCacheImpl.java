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
package org.sybila.parasim.computation.density.spawn.cpu;

import java.util.Map;
import java.util.WeakHashMap;
import org.sybila.parasim.computation.density.spawn.api.SpawnedTrajectoriesCache;
import org.sybila.parasim.model.trajectory.FractionPoint;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class SpawnedTrajectoriesCacheImpl implements SpawnedTrajectoriesCache {

    private final Map<FractionPoint, Trajectory> trajectories = new WeakHashMap<>();

    @Override
    public boolean contains(FractionPoint key) {
        synchronized(trajectories) {
            return trajectories.containsKey(key);
        }
    }

    @Override
    public Trajectory load(FractionPoint key, Trajectory trajectory) {
        synchronized(trajectories) {
            Trajectory result = trajectories.get(key);
            if (result == null) {
                trajectories.put(key, trajectory);
                return trajectory;
            } else {
                return result.getReference().getTrajectory();
            }
        }
    }

    @Override
    public boolean store(FractionPoint key, Trajectory trajectory) {
        synchronized(trajectories) {
            Trajectory result = trajectories.get(key);
            if (result == null) {
                trajectories.put(key, trajectory);
                return true;
            } else {
                return false;
            }
        }
    }

}
