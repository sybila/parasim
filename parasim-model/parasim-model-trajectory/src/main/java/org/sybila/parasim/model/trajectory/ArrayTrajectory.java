package org.sybila.parasim.model.trajectory;

import java.util.Iterator;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ArrayTrajectory extends AbstractTrajectory {

    private float[] points;
    private ArrayPointLocator pointLocator;
    private float[] times;
    
    public ArrayTrajectory(float[] points, float[] times, final int dimension) {
        this(points, times, dimension, new ArrayPointLocator() {

            public int getPointPosition(int trajectoryIndex, int pointIndex) {
                return pointIndex * dimension;
            }

            public int getTimePosition(int trajectoryIndex, int pointIndex) {
                return pointIndex;
            }
        });
    }
    
    public ArrayTrajectory(float[] points, float[] times, final int dimension, ArrayPointLocator pointLocator) {
        super(dimension, times.length);
        if (points.length % dimension != 0) {
            throw new IllegalArgumentException("The trajectory length can't be determined.");
        }
        if (points.length / dimension != times.length) {
            throw new IllegalArgumentException("The number of points doesn't correspond to the number of times.");
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
        return new ArrayPoint(points, times[ pointLocator.getTimePosition(/*ignored*/0, index)], pointLocator.getPointPosition(/*ignored*/0, index), getDimension());
    }

}
