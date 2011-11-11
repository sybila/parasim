package org.sybila.parasim.model.trajectory;

import java.util.NoSuchElementException;

/**
 * Simple implementation of the TrajectoryIterator interface.
 * Complexity of all methods depends on the implementation of the trajectory,
 * therefore no guarantees are given.
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public class SimpleTrajectoryIterator implements TrajectoryIterator {

    private Trajectory trajectory;
    private int index;

    public SimpleTrajectoryIterator(Trajectory trajectory) {
        this(trajectory, 0);
    }

    public SimpleTrajectoryIterator(Trajectory trajectory, int index) {
        if (trajectory == null) {
            throw new IllegalArgumentException("The parameter [trajectory] is NULL.");
        }
        if (index < 0 || index >= trajectory.getLength()) {
            throw new IndexOutOfBoundsException("The index is out of the range [0, " + trajectory.getLength() + "]");
        }
        this.trajectory = trajectory;
        this.index = index;
    }

    @Override
    public boolean hasNext() {
        return index < trajectory.getLength();
    }

    @Override
    public boolean hasNext(int jump)
    {
        return index+jump-1 < trajectory.getLength();
    }

    @Override
    public Point next() {
        if (index >= trajectory.getLength()) {
            throw new NoSuchElementException();
        }
        return trajectory.getPoint(index++);
    }

    @Override
    public Point next(int jump)
    {
        if (index+jump-1 >= trajectory.getLength()) {
            throw new NoSuchElementException();
        }
        index += jump;
        return trajectory.getPoint(index);
    }

    @Override
    public int getPositionOnTrajectory()
    {
        return index;
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Not supported.");
    }
}
