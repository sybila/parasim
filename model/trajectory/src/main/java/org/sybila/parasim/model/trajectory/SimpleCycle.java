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
 * Provides an standalone implementation of a TrajectoryCycle together
 * with a CyclicTrajectoryIterator.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public class SimpleCycle implements TrajectoryCycle {

    private Trajectory trajectory;
    private boolean hasCycle;
    private int cycleStartPosition;
    private int cycleEndPosition;

    public SimpleCycle() {
        hasCycle = false;
    }

    public SimpleCycle(Trajectory trajectory, int cycleStartPosition, int cycleEndPosition) {
        if (trajectory == null) {
            throw new IllegalArgumentException("The parameter trajectory is null.");
        }
        if (cycleStartPosition < 0) {
            throw new IllegalArgumentException("The parameter cycleStartPosition must be positive.");
        }
        if (cycleEndPosition <= cycleStartPosition) {
            throw new IllegalArgumentException("The parameter cycleStartPosition must smaller then cycleEndPosition.");
        }
        if (cycleEndPosition >= trajectory.getLength()) {
            throw new IllegalArgumentException("The parameter cycleEndPosition must be smaller then the length of the trajectory.");
        }
        this.trajectory = trajectory;
        this.hasCycle = true;
        this.cycleStartPosition = cycleStartPosition;
        this.cycleEndPosition = cycleEndPosition;
    }

    @Override
    public boolean hasCycle() {
        return hasCycle;
    }

    @Override
    public int getCycleStartPosition() {
        if (!hasCycle) {
            throw new UnsupportedOperationException("The trajectory does not have a cycle.");
        }
        return cycleStartPosition;
    }

    @Override
    public int getCycleEndPosition() {
        if (!hasCycle) {
            throw new UnsupportedOperationException("The trajectory does not have a cycle.");
        }
        return cycleEndPosition;
    }

    /**
     * If the trajectory has a cycle a CyclicTrajectoryIterator is returned.
     * Otherwise an exception is thrown.
     *
     * @return CyclicTrajectoryIterator.
     */
    @Override
    public CyclicTrajectoryIterator cyclicIterator() {
        if (!hasCycle) {
            throw new UnsupportedOperationException("The trajectory does not have a cycle.");
        }
        return new CyclicIterator(0);
    }

    /**
     * If the trajectory has a cycle a CyclicTrajectoryIterator is returned
     * pointing to the Point with position equal to index.
     * If there is no cycle an exception is thrown.
     *
     * @param index Index of point to which the iterator will be pointing.
     * @return CyclicTrajectoryIterator pointing with given index.
     */
    @Override
    public CyclicTrajectoryIterator cyclicIterator(int index) {
        if (!hasCycle) {
            throw new UnsupportedOperationException("The trajectory does not have a cycle.");
        }
        if (index < 0) {
            throw new IllegalArgumentException("The parameter [index] must be positive.");
        }
        return new CyclicIterator(index);
    }

    /**
     * The cyclic iterator is defined only for trajectories with a cycle.
     */
    private class CyclicIterator implements CyclicTrajectoryIterator {

        private int index;
        private int cycleLength;
        private float period;

        public CyclicIterator(int index) {
            this.index = index;
            this.period = -1;
            this.cycleLength = cycleEndPosition - cycleStartPosition;
        }

        @Override
        public boolean nextLoopsBack() {
            return (index > cycleStartPosition
                    && ((index - cycleStartPosition) % cycleLength) == 0);
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public Point next() {
            if (index < cycleEndPosition) {
                return trajectory.getPoint(index++);
            }
            if (period < 0) {
                period = trajectory.getPoint(cycleEndPosition).getTime()
                        - trajectory.getPoint(cycleStartPosition).getTime();
            }
            int cycleIndex = index - cycleStartPosition;
            int cycleCount = cycleIndex / cycleLength;
            cycleIndex = (cycleIndex % cycleLength) + cycleStartPosition;
            Point tmp = trajectory.getPoint(cycleIndex);
            ArrayPoint p = new ArrayPoint(tmp.getTime() + cycleCount * period, tmp.toArray());
            index++;
            return p;
        }

        public void remove() {
            throw new UnsupportedOperationException("Is not supported.");
        }
    }
}
