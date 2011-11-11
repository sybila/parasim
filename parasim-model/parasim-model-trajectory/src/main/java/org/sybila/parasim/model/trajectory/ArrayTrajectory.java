package org.sybila.parasim.model.trajectory;

/**
 * All methods from AbstractTrajectory have constant complexity thanks to
 * the inner arrays storing all point data.
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
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

    @Override
    public TrajectoryIterator iterator()
    {
        return new SimpleTrajectoryIterator(this, 0);
    }

    @Override
    public TrajectoryIterator iterator(int index)
    {
        return new SimpleTrajectoryIterator(this, index);
    }    

}
