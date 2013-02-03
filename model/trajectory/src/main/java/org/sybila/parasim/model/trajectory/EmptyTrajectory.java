/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
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

import java.util.NoSuchElementException;

/**
 * This class represents an empty trajectory which can be used when trajectory
 * trajectory is needed, but it will be available in the future.
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class EmptyTrajectory implements Trajectory {

    public static final EmptyTrajectory EMPTY_TRAJECTORY = new EmptyTrajectory();
    private int dimension;
    private TrajectoryReference reference = new TrajectoryReference() {

        private Trajectory trajectory = EmptyTrajectory.this;

        public Trajectory getTrajectory() {
            return trajectory;
        }

        public void setTrajectory(Trajectory trajectory) {
            this.trajectory = trajectory;
        }
    };

    public EmptyTrajectory() {
        this(-1);
    }

    public EmptyTrajectory(int dimension) {
        this.dimension = dimension;
    }

    public int getDimension() {
        if (dimension <= 0) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        return dimension;
    }

    public Point getFirstPoint() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Point getLastPoint() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getLength() {
        return 0;
    }

    public Point getPoint(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public TrajectoryReference getReference() {
        return reference;
    }

    public boolean hasPoint(int index) {
        return false;
    }

    public TrajectoryIterator iterator() {
        return new TrajectoryIterator() {

            public boolean hasNext(int jump) {
                return false;
            }

            public Point next(int jump) {
                throw new NoSuchElementException();
            }

            public int getPositionOnTrajectory() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public boolean hasNext() {
                return false;
            }

            public Point next() {
                throw new NoSuchElementException();
            }

            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    public TrajectoryIterator iterator(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
