package org.sybila.parasim.model.trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class PointTrajectory extends AbstractTrajectory {

    /** First point of the trajectory. */
    private Point initialPoint;

    public PointTrajectory(Point initialPoint) {
        super(initialPoint.getDimension(), 1);
        this.initialPoint = initialPoint;
    }

    public PointTrajectory(float time, float... data) {
        this(new ArrayPoint(time, data));
    }

    @Override
    public Point getPoint(int index) {
        if (index != 0) {
            throw new IllegalArgumentException("The point index is out of the range [0, " + (getLength() - 1) + "]");
        }
        return initialPoint;
    }

    @Override
    public String toString() {
        return "[" + initialPoint + "]";
    }
}