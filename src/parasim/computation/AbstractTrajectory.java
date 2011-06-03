package parasim.computation;

import java.util.Iterator;
import java.util.NoSuchElementException;

abstract public class AbstractTrajectory implements Trajectory {

	private int dimension;
	private int length;

	public AbstractTrajectory(int dimension, int length) {
		if (dimension <= 0) {
			throw new IllegalArgumentException("The dimension has to be a positive number.");
		}
		if (length <= 0) {
			throw new IllegalArgumentException("The length has to be a positive number.");
		}
		this.dimension = dimension;
		this.length = length;
	}

	public boolean hasPoint(int index) {
		return index >= 0 && index < getLength();
	}

	public int getDimension() {
		return dimension;
	}

	public Point getFirstPoint() {
		return getPoint(0);
	}

	public Point getLastPoint() {
		return getPoint(getLength() - 1);
	}

	public int getLength() {
		return length;
	}

	protected final void setLength(int length) {
		if (length <= 0) {
			throw new IllegalArgumentException("The length has to be a positive number.");
		}
		this.length = length;
	}

	public Iterator<Point> iterator() {
		return new TrajectoryIterator(this);
	}

	private class TrajectoryIterator implements Iterator<Point> {

		private Trajectory trajectory;
		private int index = 0;

		public TrajectoryIterator(Trajectory trajectory) {
			if (trajectory == null) {
				throw new IllegalArgumentException("The parameter [trajectory] is NULL.");
			}
			this.trajectory = trajectory;
		}

		public boolean hasNext() {
			return index < trajectory.getLength();
		}

		public Point next() {
			if (index == trajectory.getLength()) {
				throw new NoSuchElementException();
			}
			return trajectory.getPoint(index++);
		}

		public void remove() {
			throw new UnsupportedOperationException("Not supported yet.");
		}
	}
}
