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
package org.sybila.parasim.model.trajectory;

import java.util.HashMap;
import java.util.Map;

/**
 * Really simple implementation of {@link TrajectoryNeighborhood} using {@link Map}
 * as a storage.
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class MapTrajectoryNeighborhood<T extends Trajectory> implements TrajectoryNeighborhood<T> {

    Map<Trajectory, DataBlock<T>> neighborhoods;

    public MapTrajectoryNeighborhood() {
        this(new HashMap<Trajectory, DataBlock<T>>());
    }

    public MapTrajectoryNeighborhood(Map<Trajectory, DataBlock<T>> neighborhoods) {
        if (neighborhoods == null) {
            throw new IllegalArgumentException("The parameter neighborhoods is null.");
        }
        this.neighborhoods = neighborhoods;
    }

    @Override
    public DataBlock<T> getNeighbors(Trajectory trajectory) {
        return neighborhoods.get(trajectory);
    }

    public void setNeighbors(Trajectory trajectory, DataBlock<T> neighborhood) {
        if (trajectory == null) {
            throw new IllegalArgumentException("The parameter trajectory is null.");
        }
        if (neighborhood == null) {
            throw new IllegalArgumentException("The parameter neighborhood is null.");
        }
        neighborhoods.put(trajectory, neighborhood);
    }
}
