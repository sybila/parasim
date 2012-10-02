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
package org.sybila.parasim.computation.cycledetection.api;

import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class SimpleCycleDetector implements CycleDetector {

    private final Point startPoint;
    private final Point endPoint;
    private final int start;
    private final int end;

    public SimpleCycleDetector(Trajectory trajectory, int start, int end) {
        if (trajectory == null) {
            throw new IllegalArgumentException("The parameter [trajectory] is null.");
        }
        if (start < 0) {
            throw new IllegalStateException("The start index can't be lower than 0");
        }
        if (start > end) {
            throw new IllegalArgumentException("The start index [" + start + "] can't be greater than end index [" + end + "].");
        }
        if (end >= trajectory.getLength()) {
            throw new IllegalArgumentException("The end index can't be greater tjan trajectory length");
        }
        this.start = start;
        this.end = end;
        this.startPoint = trajectory.getPoint(start);
        this.endPoint = trajectory.getPoint(end);
    }

    @Override
    public boolean isCycleDetected() {
        return true;
    }

    @Override
    public Point getCycleStart() {
        return startPoint;
    }

    @Override
    public int getCycleStartPosition() {
        return start;
    }

    @Override
    public Point getCycleEnd() {
        return endPoint;
    }

    @Override
    public int getCycleEndPosition() {
        return end;
    }

}
