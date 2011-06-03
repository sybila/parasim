package parasim.computation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LinkedTrajectory extends AbstractTrajectory {

	private List<Trajectory> trajectories = new ArrayList<Trajectory>();

	public LinkedTrajectory(Trajectory trajectory) {
		super(trajectory.getDimension(), trajectory.getLength());
		trajectories.add(trajectory);
	}

	public void append(Trajectory trajectory) {
		if (trajectory == null) {
			throw new IllegalArgumentException("The parameter [trajectory] is NULL.");
		}
		if (trajectory.getFirstPoint().getTime() < trajectories.get(trajectories.size()).getLastPoint().getTime()) {
			throw new IllegalArgumentException("The time of the first point of the given trajectory is lower than the time of the last point of original trajectory.");
		}
		setLength(getLength() + trajectory.getLength());
		trajectories.add(trajectory);
	}

	@Override
	public Point getFirstPoint() {
		return trajectories.get(0).getFirstPoint();
	}

	@Override
	public Point getLastPoint() {
		return trajectories.get(trajectories.size() - 1).getLastPoint();
	}

	public Point getPoint(int index) {
		if (index < 0) {
			throw new IllegalArgumentException("The index has to be non negative number.");
		}
		if (index >= getLength()) {
			throw new IllegalArgumentException("The index has to be lower than trajectory length.");
		}
		int length = 0;
		for (Trajectory t : trajectories) {
			if (index < length + t.getLength()) {
				index -= length;
				return t.getPoint(index);
			}
			length += t.getLength();
		}
		return null;
	}

	@Override
	public Iterator<Point> iterator() {
		return new LinkedTrajectoryIterator(this);
	}

	private class LinkedTrajectoryIterator implements Iterator<Point> {

		private LinkedTrajectory trajectory;
		private int trajectoryIndex = 0;
		private Iterator<Point> iterator;

		public LinkedTrajectoryIterator(LinkedTrajectory trajectory) {
			if (trajectory == null) {
				throw new IllegalArgumentException("The parameter [trajectory] is NULL.");
			}
			this.trajectory = trajectory;
			this.iterator = trajectories.get(0).iterator();
		}

		public boolean hasNext() {
			return iterator != null && iterator.hasNext();
		}

		public Point next() {
			Point point = iterator.next();
			if (!iterator.hasNext()) {
				trajectoryIndex++;
				if (trajectoryIndex < trajectory.trajectories.size()) {
					iterator = trajectory.trajectories.get(trajectoryIndex).iterator();
				}
				else {
					iterator = null;
				}
			}
			return point;
		}

		public void remove() {
			throw new UnsupportedOperationException("Not supported yet.");
		}
	}
}
