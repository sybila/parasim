package org.sybila.parasim.model.trajectory;

import java.util.Iterator;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ArrayTrajectory extends AbstractTrajectory {

    private float[] points;
    private float[] times;
    
    public ArrayTrajectory(float[] points, float[] times, int dimension) {
        super(dimension, times.length);
        if (points.length % dimension != 0) {
            throw new IllegalArgumentException("The trajectory length can't be determined.");
        }
        if (points.length / dimension != times.length) {
            throw new IllegalArgumentException("The number of points doesn't correspond to the number of times.");
        }
        this.points = points;
        this.times = times;
    }

    @Override
    public Point getPoint(int index) {
        if (index < 0 || index >= getLength()) {
            throw new IllegalArgumentException("The point index is out of the range [0, " + (getLength() - 1) + "]");
        }
        return new ArrayPoint(points, times[index], index * getDimension(), getDimension());
    }

}
