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

import java.io.Serializable;

/**
 * All methods from AbstractTrajectory have constant complexity thanks to the
 * inner arrays storing all point data.
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public class ArrayTrajectory extends AbstractTrajectory {

    private float[] points;
    private ArrayPointLocator pointLocator;

    public float[] getPoints() {
        return points;
    }

    public float[] getTimes() {
        return times;
    }

    private float[] times;
    private final Point parentPoint;

    public int getVarDimension() {
        return dimension;
    }

    private final int dimension;

    public ArrayTrajectory(float[] points, float[] times, final int dimension) {
        this(points, times, dimension, times.length, new DefaultArrayPointLocator(dimension));
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
        this.parentPoint = null;
        this.dimension = dimension;
    }

    /**
     *
     * @param parent starting point
     * @param points simulated points in format [variable1-value1, variable2-value1, variable1-value2, variable2-value2...]
     * @param times of length points/number of variables
     * @param dimension Number of variables in one point of this trajectory
     */
    public ArrayTrajectory(Point parent, float[] points, float[] times, final int dimension) {
        this(parent, points, times, dimension, times.length, new DefaultArrayPointLocator(dimension));
    }

    public ArrayTrajectory(Point parent, float[] points, float[] times, final int dimension, final int length, ArrayPointLocator pointLocator) {
        super(parent.getDimension(), length);
        if (dimension > parent.getDimension()) {
            throw new IllegalArgumentException("The dimension of parent point has to at least the dimension of the given data.");
        }
        if (points.length % dimension != 0) {
            throw new IllegalArgumentException("The trajectory length can't be determined.");
        }
        if (pointLocator == null) {
            throw new IllegalArgumentException("The parameter pointLocator is null.");
        }
        this.points = points;
        this.times = times;
        this.pointLocator = pointLocator;
        this.parentPoint = parent;
        this.dimension = dimension;
    }

    @Override
    public Point getPoint(int index) {
        if (index < 0 || index >= getLength()) {
            throw new IllegalArgumentException("The point index is out of the range [0, " + (getLength() - 1) + "]");
        }
        if (parentPoint == null) {
            return new ArrayPoint(times[pointLocator.getTimePosition(index)], points, pointLocator.getPointPosition(index), getDimension());
        } else {
            return new InheritingPoint(parentPoint, times[pointLocator.getTimePosition(index)], points, pointLocator.getPointPosition(index), dimension);
        }
    }

    @Override
    public TrajectoryIterator iterator() {
        return new SimpleTrajectoryIterator(this, 0);
    }

    @Override
    public TrajectoryIterator iterator(int index) {
        return new SimpleTrajectoryIterator(this, index);
    }

    public static final class DefaultArrayPointLocator implements ArrayPointLocator, Serializable {

        private final int dimension;

        public DefaultArrayPointLocator(int dimension) {
            this.dimension = dimension;
        }

        @Override
        public int getPointPosition(int pointIndex) {
            return pointIndex * dimension;
        }

        @Override
        public int getTimePosition(int pointIndex) {
            return pointIndex;
        }
    }
}
