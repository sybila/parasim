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
package org.sybila.parasim.model.trajectory;

/**
 * Enables appending and adding to the DataBlock.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public interface MutableDataBlock extends DataBlock<Trajectory> {
    /**
     * Appends all the trajectories to the end of the ones already in the
     * DataBlock. If the number of trajectories to be appended and the ones
     * already in DataBlock differ or they are not Mutable a exception is thrown.
     *
     * @param trajectories Trajectories to be appended.
     */
    void append(DataBlock<Trajectory> trajectories);

    /**
     * Adds all the trajectories into the DataBlock.
     * @param trajectories Trajectories to be added.
     */
    void merge(DataBlock<Trajectory> trajectories);

}
