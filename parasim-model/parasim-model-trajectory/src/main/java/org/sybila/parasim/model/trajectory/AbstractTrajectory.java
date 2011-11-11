package org.sybila.parasim.model.trajectory;

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
    public TrajectoryIterator iterator() {
        return iterator(0);
    }

    @Override
    public TrajectoryIterator iterator(int index) {
        return new SimpleTrajectoryIterator(this, index);
    }    
    
    protected final void setLength(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("The length has to be a positive number.");
        }
        this.length = length;
    }    
    
}
