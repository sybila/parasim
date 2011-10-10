package org.sybila.parasim.model.trajectory;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
abstract public class AbstractTrajectory implements Trajectory{

    private int dimension;
    private int length;
    private TrajectoryReference reference;

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

    @Override
    public boolean hasPoint(int index) {
        return index >= 0 && index < getLength();
    }

    @Override
    public int getDimension() {
        return dimension;
    }

    @Override
    public Point getFirstPoint() {
        return getPoint(0);
    }

    @Override
    public Point getLastPoint() {
        return getPoint(getLength() - 1);
    }

    @Override
    public int getLength() {
        return length;
    }
    
    @Override
    public TrajectoryReference getReference() {
        if (reference == null) {
            final Trajectory thisTrajectory = this;
            reference = new TrajectoryReference() {

                private Trajectory referencedTrajectory = thisTrajectory;
                
                @Override
                public Trajectory getTrajectory() {
                    return referencedTrajectory;
                }

                @Override
                public void setTrajectory(Trajectory trajectory) {
                    referencedTrajectory = trajectory;
                }
            };
        }
        return reference;
    }    
    
    @Override
    public Iterator<Point> iterator() {
        return iterator(0);
    }

    @Override
    public Iterator<Point> iterator(int index) {
        return new TrajectoryIterator(this, index);
    }

    private class TrajectoryIterator implements Iterator<Point> {

        private Trajectory trajectory;
        private int index;

        public TrajectoryIterator(Trajectory trajectory) {
            this(trajectory, 0);
        }

        public TrajectoryIterator(Trajectory trajectory, int index) {
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
        public Point next() {
            if (index == trajectory.getLength()) {
                throw new NoSuchElementException();
            }
            return trajectory.getPoint(index++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    protected final void setLength(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("The length has to be a positive number.");
        }
        this.length = length;
    }    
    
}
