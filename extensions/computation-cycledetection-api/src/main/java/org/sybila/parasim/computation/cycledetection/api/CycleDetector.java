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

/**
 * Enables cycle detecion on a trajectory while specifying how many points
 * at most to inspect on the trajectory.
 *
 * A cycle is understood as two points on the trajectory being marked as
 * similar.
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public interface CycleDetector {

    public static CycleDetector CYCLE_IS_NOT_DETECTED = new CycleDetector() {

        @Override
        public boolean isCycleDetected() {
            return false;
        }

        @Override
        public Point getCycleStart() {
            throw new IllegalStateException("The cycle hasn't been detected.");
        }

        @Override
        public int getCycleStartPosition() {
            throw new IllegalStateException("The cycle hasn't been detected.");
        }

        @Override
        public Point getCycleEnd() {
            throw new IllegalStateException("The cycle hasn't been detected.");
        }

        @Override
        public int getCycleEndPosition() {
            throw new IllegalStateException("The cycle hasn't been detected.");
        }

        @Override
        public int getCycleSize() {
            return 0;
        }
    };

    /**
     * Before a cycle has been detected false is returned, if a cycle
     * is detected true will be returned from that time on.
     *
     * @return True if cycle has been already detected, false otherwise.
     */
    boolean isCycleDetected();

    /**
     * If a cycle has been detected on a trajectory then two points have been
     * found similar. In this case the first one is returned.
     * If no cycle is detected null is returned.
     *
     * @return First of the two similar points or null.
     */
    Point getCycleStart();

    int getCycleStartPosition();

    /**
     * If a cycle has been detected on a trajectory then two points have been
     * found similar. In this case the second one is returned.
     * If no cycle is detected null is returned.
     *
     * @return Second of the two similar points or null
     */
    Point getCycleEnd();

    int getCycleEndPosition();

    int getCycleSize();

}
