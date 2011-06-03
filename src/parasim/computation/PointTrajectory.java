package parasim.computation;

import java.util.ArrayList;

public class PointTrajectory extends AbstractTrajectory implements Trajectory
{

    /** First point of the trajectory. */
    private Point initialPoint;    
    /** List of trajectory segments which are subtrajectories. */
    private ArrayList<Trajectory> segments;
    /** Number of points in all segments together. */
    private int length;

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