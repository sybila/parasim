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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class LinkedTrajectoryTest extends AbstractTrajectoryTest<LinkedTrajectory> {

    private LinkedTrajectory trajectory;
    private Point[] points;

    @BeforeMethod
    public void setUp() {
        int pointIndex = 0;
        points = new Point[(int)((1 + LENGTH) * LENGTH / 2)];
        for(int length=1; length<=LENGTH; length++) {
            float[] data = new float[length * DIMENSION];
            float[] times = new float[length];
            for(int p=0; p<length; p++) {
                for(int dim=0; dim<DIMENSION; dim++) {
                    data[p * DIMENSION + dim] = (float) (length + p * 0.01 + 0.0001 * dim);
                    times[p] = (float) (length + p * 0.01);
                }
                points[pointIndex] = new ArrayPoint(times[p], data, p * DIMENSION, DIMENSION);
                pointIndex++;
            }

            if (length == 1) {
                trajectory = new LinkedTrajectory(new ArrayTrajectory(data, times, DIMENSION));
            }
            else {
                trajectory.append(new ArrayTrajectory(data, times, DIMENSION));
            }

        }
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
    public void testCreateAndUpdateReference() {
        Trajectory trajectory = new PointTrajectory(0, 0.1f, 0.2f);
        LinkedTrajectory.createAndUpdateReference(trajectory);
        assertEquals(trajectory.getReference().getTrajectory().getClass(), LinkedTrajectory.class);
    }
}
