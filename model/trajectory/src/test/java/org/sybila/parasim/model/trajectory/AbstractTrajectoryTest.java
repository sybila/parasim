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

import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class AbstractTrajectoryTest<T extends Trajectory> {
    
    protected static final int LENGTH = 10;
    protected static final int DIMENSION = 10;

    
    protected void testPointSequenceWithIterator(Trajectory trajectory, Point[] points) {
        assertEquals(trajectory.getLength(), points.length);
        int index = 0;
        for(Point point: trajectory) {
            assertPoint(points[index], point);
            index++;
        }
    }
    
    protected void testPointSequenceWithGetMethod(Trajectory trajectory, Point[] points) {
        assertEquals(trajectory.getLength(), points.length);
        for (int i=0; i<trajectory.getLength(); i++) {
            assertPoint(points[i], trajectory.getPoint(i));
        }
    }
    
    private void assertPoint(Point expected, Point actual) {
        assertEquals(expected.getDimension(), actual.getDimension());
        assertEquals(expected.getTime(), actual.getTime());
        for (int dim=0; dim<expected.getDimension(); dim++) {
            assertEquals(expected.getValue(dim), expected.getValue(dim), "The value on dimension <" + dim + ">.");
        }
    }
    
}
