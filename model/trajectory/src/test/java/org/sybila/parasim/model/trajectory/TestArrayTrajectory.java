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

import junit.framework.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestArrayTrajectory extends AbstractTrajectoryTest<ArrayTrajectory> {

    private Point[] points;
    private Trajectory trajectory;

    @BeforeTest
    public void setUp() {
        points = new Point[LENGTH];
        float[] data = new float[LENGTH * DIMENSION];
        float[] times = new float[LENGTH];
        for(int p=0; p<LENGTH; p++) {
            for(int dim=0; dim<DIMENSION; dim++) {
                data[p * DIMENSION + dim] = (float) (p + 0.01 * dim);
                times[p] = p;
            }
            points[p] = new ArrayPoint(p, data, p * DIMENSION, DIMENSION);
        }
        trajectory = new ArrayTrajectory(data, times, DIMENSION);
    }

    @Test
    public void testPointSequenceWithIterator() {
        super.testPointSequenceWithIterator(trajectory, points);
    }

    @Test
    public void testPointSequenceWithGetMethod() {
        super.testPointSequenceWithGetMethod(trajectory, points);
    }

    @Test
    public void testParentPoint() {
        Point parent = new ArrayPoint(0, 100, 200, 300, 400);
        float[] data = new float[] { 1, 2, 3, 4 };
        float[] times = new float[] { 1, 2 };
        Trajectory trajectory = new ArrayTrajectory(parent, data, times, 2);
        Assert.assertEquals(2, trajectory.getLength());
        for (Point p: trajectory) {
            for (int dim=2; dim < parent.getDimension(); dim++) {
                Assert.assertEquals(parent.getValue(dim), p.getValue(dim));
            }
        }
    }
}
