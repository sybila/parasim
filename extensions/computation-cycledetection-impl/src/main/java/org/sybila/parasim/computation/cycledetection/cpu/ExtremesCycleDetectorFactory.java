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
package org.sybila.parasim.computation.cycledetection.cpu;

import org.sybila.parasim.computation.cycledetection.api.CycleDetectedDataBlock;
import org.sybila.parasim.computation.cycledetection.api.CycleDetector;
import org.sybila.parasim.computation.cycledetection.api.CycleDetectorFactory;
import org.sybila.parasim.computation.cycledetection.api.SimpleCycleDetector;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.util.ExtremesQueue;

 /**
 * The extremes cycle detection provides means to detect cycles on trajectories.
 * A cycle is considered the repetition of points who's values are close enough
 * on all spatial dimensions. Close enough is specified by relative tolerance
 * which is the same for all dimensions.
 *
 * To detect a cycle the extremes of the trajectory (minima, maxima) are
 * first detected as canonical points to compare. This works only if the
 * points are actualy provided to the detectCycle method with a small enough
 * tolerance. The minima and maxima are detected automaticaly from the last
 * two tested points.
 *
 * The mode array specifies if the cycle is to be detected in minima, maxima or
 * both on each dimension.
 *
 * For each dimension a queue is maintained holding the last detected extreme
 * points on this dimension and the next time a new extreme is detected
 * for this dimension the new point is compared to all the points in the queue.
 * If a cycle is not found the new point is then inserted into the queue.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ExtremesCycleDetectorFactory implements CycleDetectorFactory {

    private final float relativeTolerance;

    public ExtremesCycleDetectorFactory(float relativeTolerance) {
        this.relativeTolerance = relativeTolerance;
    }

    @Override
    public CycleDetector detect(Trajectory trajectory) {
        ExtremesQueue<Point, Float>[] extremes = new ExtremesQueue[trajectory.getDimension()];
        for (int dim=0; dim<extremes.length; dim++) {
            extremes[dim] = new ExtremesQueue(new DimensionRetriever(dim));
        }
        for (int dim=0; dim < extremes.length; dim++) {
            int length = 0;
            for (Point point: trajectory) {
                extremes[dim].offer(point);
                length++;
            }
        }
        for (ExtremesQueue<Point, ?> extremesInDimension: extremes) {
            for (Point p: extremesInDimension) {
                int alreadyFoundIndex = -1;
                int indexInTrajectory = -1;
                for (Point main: trajectory) {
                    indexInTrajectory++;
                    if (p.getTime() > main.getTime()) {
                        continue;
                    }
                    if (main.equals(p, relativeTolerance)) {
                        if (alreadyFoundIndex == -1) {
                            alreadyFoundIndex = indexInTrajectory;
                        } else {
                            return new SimpleCycleDetector(trajectory, alreadyFoundIndex, indexInTrajectory - 1);
                        }
                    }
                }
            }
        }
        return CycleDetector.CYCLE_IS_NOT_DETECTED;
    }

    @Override
    public <T extends Trajectory> CycleDetectedDataBlock<T> detect(DataBlock<T> trajectories) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static class DimensionRetriever implements ExtremesQueue.Evaluator<Point, Float> {

        private final int dimension;

        private DimensionRetriever(int dimension) {
            this.dimension = dimension;
        }

        @Override
        public Float evaluate(Point item) {
            return item.getValue(dimension);
        }

    }

}
