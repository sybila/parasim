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
package org.sybila.parasim.computation.simulation.api;

import java.util.Iterator;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ArraySimulatedDataBlock<T extends Trajectory> implements SimulatedDataBlock<T> {

    private DataBlock<T> dataBlock;
    private Status[] statuses;

    public ArraySimulatedDataBlock(DataBlock<T> dataBlock, Status[] statuses) {
        if (dataBlock == null) {
            throw new IllegalArgumentException("The parameter dataBlock is null.");
        }
        if (statuses == null) {
            throw new IllegalArgumentException("The parameter statuses is null.");
        }
        if (dataBlock.size() != statuses.length) {
            throw new IllegalArgumentException("The number of trajectories in data block doesn't match with the number statuses.");
        }
        this.dataBlock = dataBlock;
        this.statuses = statuses;
    }

    @Override
    public Status getStatus(int index) {
        return statuses[index];
    }

    @Override
    public T getTrajectory(int index) {
        return dataBlock.getTrajectory(index);
    }

    @Override
    public Iterator<T> iterator() {
        return dataBlock.iterator();
    }

    @Override
    public int size() {
        return dataBlock.size();
    }
}
