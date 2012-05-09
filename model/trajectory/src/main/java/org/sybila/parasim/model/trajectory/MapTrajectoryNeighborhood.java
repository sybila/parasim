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
import java.util.Iterator;
import java.util.Map;

/**
 * Really simple implementation of {@link TrajectoryNeighborhood} using {@link Map}
 * as a storage.
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class MapTrajectoryNeighborhood implements TrajectoryNeighborhood {

    Map<Trajectory, DataBlock<Trajectory>> neighborhoods;

    public MapTrajectoryNeighborhood() {
        this(new HashMap<Trajectory, DataBlock<Trajectory>>());
    }

    public MapTrajectoryNeighborhood(Map<Trajectory, DataBlock<Trajectory>> neighborhoods) {
        if (neighborhoods == null) {
            throw new IllegalArgumentException("The parameter neighborhoods is null.");
        }
        this.neighborhoods = neighborhoods;
    }

    @Override
    public DataBlock<Trajectory> getNeighbors(Trajectory trajectory) {
        final DataBlock<Trajectory> toReturn = neighborhoods.get(trajectory);
        return new DataBlock<Trajectory>() {
            public Trajectory getTrajectory(int index) {
                return toReturn.getTrajectory(index).getReference().getTrajectory();
            }
            public int size() {
                return toReturn.size();
            }
            public Iterator<Trajectory> iterator() {
                return toReturn.iterator();
            }
        };
    }

    public void setNeighbors(Trajectory trajectory, DataBlock<Trajectory> neighborhood) {
        if (trajectory == null) {
            throw new IllegalArgumentException("The parameter trajectory is null.");
        }
        if (neighborhood == null) {
            throw new IllegalArgumentException("The parameter neighborhood is null.");
        }
        neighborhoods.put(trajectory, neighborhood);
    }
}