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
package org.sybila.parasim.computation.cycledetection.cpu;

import java.util.Arrays;
import org.sybila.parasim.computation.cycledetection.api.CycleDetector;
import org.sybila.parasim.computation.cycledetection.api.CycleDetectorFactory;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.LinkedTrajectory;
import org.sybila.parasim.model.trajectory.ListTrajectory;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.testng.Assert;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstracCycleDetectorFactoryTest {

    protected final void checkCycle(CycleDetectorFactory factory) {
        LinkedTrajectory trajectory = LinkedTrajectory.createAndUpdateReference(trajectory(10, 1f, 1f, 2f, 3f, 4f));
        Point[] cycle = new Point[5];
        for (int i=0; i<cycle.length; i++) {
            cycle[i] = new ArrayPoint(10 + i, 11 + i, 22 + i, 33 + i, 44 + i);
        }
        trajectory.append(cycle(5, cycle));
        CycleDetector detector = factory.detect(trajectory);
        Assert.assertTrue(detector.isCycleDetected(), "The equilibrium hasn't been correctly detected.");
        Assert.assertEquals(detector.getCycleStartPosition(), 14);
        Assert.assertEquals(detector.getCycleEndPosition(), 18);
    }

    protected final void checkEqulibrium(CycleDetectorFactory factory) {
        LinkedTrajectory trajectory = LinkedTrajectory.createAndUpdateReference(trajectory(10, 1f, 1f, 2f, 3f, 4f));
        Point inCycle = new ArrayPoint(trajectory.getLastPoint().getTime(), trajectory.getLastPoint().toArray());
        trajectory.append(cycle(5, inCycle));
        CycleDetector detector = factory.detect(trajectory);
        Assert.assertTrue(detector.isCycleDetected(), "The equilibrium hasn't been correctly detected.");
        Assert.assertEquals(detector.getCycleStartPosition(), 9, "The cycle start position doesn't match.");
        Assert.assertEquals(detector.getCycleEndPosition(), 9, "The cycle end position doesn't match.");
    }

    protected final void checkNoCycle(CycleDetectorFactory factory) {
        LinkedTrajectory trajectory = LinkedTrajectory.createAndUpdateReference(trajectory(10, 1f, 1f, 2f, 3f, 4f));
        CycleDetector detector = factory.detect(trajectory);
        Assert.assertFalse(detector.isCycleDetected());
    }

    protected final LinkedTrajectory cycle(int repeats, Point... points) {
        LinkedTrajectory trajectory = LinkedTrajectory.createAndUpdateReference(trajectory(points));
        float timeDiff = 1;
        if (points.length > 1) {
            timeDiff = Math.abs(points[0].getTime() - points[1].getTime());
        }
        Point[] previousPoints = points;
        for (int i=1; i<repeats; i++) {
            Point[] newPoints = new Point[points.length];
            for (int j=0; j<newPoints.length; j++) {
                newPoints[j] = new ArrayPoint(previousPoints[j].getTime() + previousPoints.length * timeDiff, previousPoints[j].toArray());
            }
            trajectory.append(trajectory(newPoints));
            previousPoints = newPoints;
        }
        return trajectory;
    }

    protected final Trajectory trajectory(int length, float timeDiff, float... data) {
        Point[] points = new Point[length];
        for (int i=0; i<length; i++) {
            float[] newData = new float[data.length];
            for (int j=0; j<newData.length; j++) {
                newData[j] = (i+1) * data[j];
            }
            points[i] = new ArrayPoint(i * timeDiff, newData);
        }
        return trajectory(points);
    }

    protected final Trajectory trajectory(Point... points) {
        return new ListTrajectory(Arrays.asList(points));
    }

}
