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
package org.sybila.parasim.computation.verification.cpu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.sybila.parasim.computation.cycledetection.api.CycleDetector;
import org.sybila.parasim.model.trajectory.AbstractTrajectory;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.verification.Property;

public class ExpandedTrajectory extends AbstractTrajectory {

    private final Trajectory trajectory;
    private final CycleDetector detector;
    private final List<Point> expanded;

    public ExpandedTrajectory(Trajectory trajectory, CycleDetector detector, Property formula) {
        super(trajectory.getDimension(), trajectory.getLength());
        this.trajectory = trajectory;
        this.detector = detector;
        this.expanded = expand(trajectory, detector, formula);
        if (!this.expanded.isEmpty()) {
            this.setLength(detector.getCycleEndPosition() + 1 + expanded.size());
        }
    }

    @Override
    public Point getPoint(int index) {
        if (expanded.isEmpty() || index <= detector.getCycleEndPosition()) {
            return trajectory.getPoint(index);
        } else {
            return expanded.get(index - detector.getCycleEndPosition() - 1);
        }
    }

    private static List<Point> expand(Trajectory trajectory, CycleDetector detector, Property formula) {
        if (trajectory.getLastPoint().getTime() >= formula.getTimeNeeded() || !detector.isCycleDetected()) {
            return Collections.EMPTY_LIST;
        }
        List<Point> result = new ArrayList<>();
        float cycleTime = detector.getCycleSize() == 1 ? 1f : detector.getCycleEnd().getTime() - detector.getCycleEnd().getTime();
        int expansionPosition = 0;
        do {
            Point current = trajectory.getPoint(detector.getCycleStartPosition() + expansionPosition % detector.getCycleSize());
            int numOfCycles = expansionPosition / detector.getCycleSize();
            result.add(new ArrayPoint(current.getTime() + cycleTime * (numOfCycles + 1), current.toArray()));
            expansionPosition++;
        } while(result.get(result.size() - 1).getTime() < formula.getTimeNeeded());
        return result;
    }

}
