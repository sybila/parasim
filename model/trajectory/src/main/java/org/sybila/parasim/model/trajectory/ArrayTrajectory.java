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

/**
 * All methods from AbstractTrajectory have constant complexity thanks to
 * the inner arrays storing all point data.
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public class ArrayTrajectory extends AbstractTrajectory {

    private float[] points;
    private ArrayPointLocator pointLocator;
    private float[] times;

    public ArrayTrajectory(float[] points, float[] times, final int dimension) {
        this(points, times, dimension, times.length, new ArrayPointLocator() {

            public int getPointPosition(int pointIndex) {
                return pointIndex * dimension;
            }

            public int getTimePosition(int pointIndex) {
                return pointIndex;
            }
        });
    }

    public ArrayTrajectory(float[] points, float[] times, final int dimension, final int length, ArrayPointLocator pointLocator) {
        super(dimension, length);
        if (points.length % dimension != 0) {
            throw new IllegalArgumentException("The trajectory length can't be determined.");
        }
        if (pointLocator == null) {
            throw new IllegalArgumentException("The parameter pointLocator is null.");
        }
        this.points = points;
        this.times = times;
        this.pointLocator = pointLocator;
    }

    @Override
    public Point getPoint(int index) {
        if (index < 0 || index >= getLength()) {
            throw new IllegalArgumentException("The point index is out of the range [0, " + (getLength() - 1) + "]");
        }
        return new ArrayPoint(times[pointLocator.getTimePosition(index)], points, pointLocator.getPointPosition(index), getDimension());
    }

    @Override
    public TrajectoryIterator iterator() {
        return new SimpleTrajectoryIterator(this, 0);
    }

    @Override
    public TrajectoryIterator iterator(int index) {
        return new SimpleTrajectoryIterator(this, index);
    }
}
