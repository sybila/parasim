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

/**
 * Combines the implementation of a LinkedTrajectory and a SimpleCycle to form
 * a Trajectory with a TrajectoryCycle.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public class LinkedCyclicTrajectory extends LinkedTrajectory implements CyclicTrajectory {

    private SimpleCycle cycle;

    public LinkedCyclicTrajectory(Trajectory trajectory) {
        super(trajectory);
        cycle = new SimpleCycle();
    }

    public LinkedCyclicTrajectory(Trajectory trajectory, int cycleStartPosition, int cycleEndPosition) {
        super(trajectory);
        cycle = new SimpleCycle(trajectory, cycleStartPosition, cycleEndPosition);
    }

    public static LinkedCyclicTrajectory createAndUpdateReference(Trajectory trajectory) {
        LinkedCyclicTrajectory linkedCyclicTrajectory = new LinkedCyclicTrajectory(trajectory);
        trajectory.getReference().setTrajectory(trajectory);
        return linkedCyclicTrajectory;
    }

    public static LinkedCyclicTrajectory createAndUpdateReference(Trajectory trajectory, int cycleStartPosition, int cycleEndPosition) {
        LinkedCyclicTrajectory linkedCyclicTrajectory = new LinkedCyclicTrajectory(trajectory, cycleStartPosition, cycleEndPosition);
        trajectory.getReference().setTrajectory(trajectory);
        return linkedCyclicTrajectory;
    }

    public boolean hasCycle() {
        return cycle.hasCycle();
    }

    public int getCycleStartPosition() {
        return cycle.getCycleStartPosition();
    }

    public int getCycleEndPosition() {
        return cycle.getCycleEndPosition();
    }

    public CyclicTrajectoryIterator cyclicIterator() {
        return cycle.cyclicIterator();
    }

    public CyclicTrajectoryIterator cyclicIterator(int index) {
        return cycle.cyclicIterator(index);
    }
}