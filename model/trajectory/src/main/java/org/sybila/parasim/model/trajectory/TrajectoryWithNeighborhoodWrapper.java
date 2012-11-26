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

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TrajectoryWithNeighborhoodWrapper implements TrajectoryWithNeighborhood {

    private final Trajectory trajectory;
    private final DataBlock<Trajectory> neighbors;

    private TrajectoryWithNeighborhoodWrapper(Trajectory trajectory, DataBlock<Trajectory> neighbors) {
        if (trajectory == null) {
            throw new IllegalArgumentException("The parameter [trajectory] is null.");
        }
        if (neighbors == null) {
            throw new IllegalArgumentException("The parameter [neighbors] is null.");
        }
        this.trajectory = trajectory;
        this.neighbors = neighbors;
    }

    public static TrajectoryWithNeighborhood createAndUpdateReference(Trajectory trajectory, DataBlock<Trajectory> neighbors) {
        TrajectoryWithNeighborhoodWrapper wrapper = new TrajectoryWithNeighborhoodWrapper(trajectory, neighbors);
        trajectory.getReference().setTrajectory(wrapper);
        return wrapper;
    }

    @Override
    public DataBlock<Trajectory> getNeighbors() {
        return neighbors;
    }

    @Override
    public int getDimension() {
        return trajectory.getDimension();
    }

    @Override
    public Point getFirstPoint() {
        return trajectory.getFirstPoint();
    }

    @Override
    public Point getLastPoint() {
        return trajectory.getLastPoint();
    }

    @Override
    public int getLength() {
        return trajectory.getLength();
    }

    @Override
    public Point getPoint(int index) {
        return trajectory.getPoint(index);
    }

    @Override
    public TrajectoryReference getReference() {
        return trajectory.getReference();
    }

    @Override
    public boolean hasPoint(int index) {
        return trajectory.hasPoint(index);
    }

    @Override
    public TrajectoryIterator iterator() {
        return trajectory.iterator();
    }

    @Override
    public TrajectoryIterator iterator(int index) {
        return trajectory.iterator(index);
    }

    @Override
    public Trajectory withoutNeighbors() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
