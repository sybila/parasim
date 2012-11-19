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
abstract public class AbstractTrajectory implements TrajectoryWithNeighborhood {

    private final int dimension;
    private int length;
    private TrajectoryReference reference;
    private final DataBlock<Trajectory> neighborhood;

    public AbstractTrajectory(int dimension, int length) {
        this(DataBlock.EMPTY_DATABLOCK, dimension, length);
    }

    public AbstractTrajectory(DataBlock<Trajectory> neighborhood, int dimension, int length) {
        if (dimension <= 0) {
            throw new IllegalArgumentException("The dimension has to be a positive number.");
        }
        if (length <= 0) {
            throw new IllegalArgumentException("The length has to be a positive number.");
        }
        if (neighborhood == null) {
            throw new IllegalArgumentException("The neighborhood is null.");
        }
        this.dimension = dimension;
        this.length = length;
        this.neighborhood = neighborhood;
    }

    @Override
    public boolean hasPoint(int index) {
        return index >= 0 && index < getLength();
    }

    @Override
    public int getDimension() {
        return dimension;
    }

    @Override
    public Point getFirstPoint() {
        return getPoint(0);
    }

    @Override
    public Point getLastPoint() {
        return getPoint(getLength() - 1);
    }

    @Override
    public int getLength() {
        return length;
    }

    public DataBlock<Trajectory> getNeighbors() {
        return neighborhood;
    }

    @Override
    public TrajectoryReference getReference() {
        if (reference == null) {
            final Trajectory thisTrajectory = this;
            reference = new TrajectoryReference() {

                private volatile Trajectory referencedTrajectory = thisTrajectory;

                @Override
                public Trajectory getTrajectory() {
                    return referencedTrajectory;
                }

                @Override
                public void setTrajectory(Trajectory trajectory) {
                    referencedTrajectory = trajectory;
                }
            };
        }
        return reference;
    }

    @Override
    public TrajectoryIterator iterator() {
        return iterator(0);
    }

    @Override
    public TrajectoryIterator iterator(int index) {
        return new SimpleTrajectoryIterator(this, index);
    }

    protected final void setLength(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("The length has to be a positive number.");
        }
        this.length = length;
    }
}
