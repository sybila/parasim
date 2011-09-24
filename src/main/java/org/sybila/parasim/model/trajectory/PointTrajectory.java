package org.sybila.parasim.model.trajectory;

import java.util.ArrayList;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class PointTrajectory extends AbstractTrajectory implements Trajectory
{

    /** First point of the trajectory. */
    private Point initialPoint;    

    PointTrajectory(Point initialPoint) {
		super(initialPoint.getDimension(), 1);
        this.initialPoint = initialPoint;
    }

	public Point getPoint(int index) {
		if (index != 0) {
			throw new IllegalArgumentException("The point index is out of the range [0, " + (getLength() - 1) + "]");
		}
		return initialPoint;
	}
	
}