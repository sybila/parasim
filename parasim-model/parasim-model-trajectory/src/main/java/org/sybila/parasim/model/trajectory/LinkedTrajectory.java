package org.sybila.parasim.model.trajectory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class LinkedTrajectory extends AbstractTrajectory {

    private List<Trajectory> trajectories = new ArrayList<Trajectory>();

    public LinkedTrajectory(Trajectory trajectory) {
        super(trajectory.getDimension(), trajectory.getLength());
        trajectories.add(trajectory);
    }

    /**
     * Appends the given trajectory on the end of this trajectory. After calling
     * this method, the reference of the given trajectory will point to this trajectory.
     * 
     * @param trajectory the trajectory which will be appended
     */
    public void append(Trajectory trajectory) {
        if (trajectory == null) {
            throw new IllegalArgumentException("The parameter [trajectory] is NULL.");
        }
        if (trajectory.getFirstPoint().getTime() < trajectories.get(trajectories.size() - 1).getLastPoint().getTime()) {
            throw new IllegalArgumentException("The time of the first point of the given trajectory is lower than the time of the last point of original trajectory.");
        }
        setLength(getLength() + trajectory.getLength());
        trajectories.add(trajectory);
        trajectory.getReference().setTrajectory(this);
    }

    @Override
    public Point getFirstPoint() {
        return trajectories.get(0).getFirstPoint();
    }

    @Override
    public Point getLastPoint() {
        return trajectories.get(trajectories.size() - 1).getLastPoint();
    }

    @Override
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
    public TrajectoryIterator iterator() {
        return iterator(0);
    }

    @Override
    public TrajectoryIterator iterator(int index) {
        return new LinkedTrajectoryIterator(this, index);
    }

    private class LinkedTrajectoryIterator implements TrajectoryIterator {

        private LinkedTrajectory trajectory;
        private int trajectoryIndex = 0;
        private TrajectoryIterator iterator;
        /** positionOnTrajectory on the total LinkedTrajectory */
        private int absolutePointIndex = 0;        

        public LinkedTrajectoryIterator(LinkedTrajectory trajectory, int index) {
            if (trajectory == null) {
                throw new IllegalArgumentException("The parameter [trajectory] is NULL.");
            }
            if (index < 0 || index >= trajectory.getLength()) {
                throw new IndexOutOfBoundsException("The index is out of the range [0, " + trajectory.getLength() + "]");
            }
            this.trajectory = trajectory;
            int startIndex = 0;
            for (Trajectory t : trajectories) {
                if (index >= startIndex && index < startIndex + t.getLength()) {
                    iterator = t.iterator(index - startIndex);
                    absolutePointIndex = index;                    
                }
                trajectoryIndex++;
                startIndex += t.getLength();
            }
        }

        @Override
        public boolean hasNext() {
            return iterator != null && iterator.hasNext();
        }

        @Override
        public boolean hasNext(int jump)
        {
            if (iterator != null)
            {
                if (iterator.hasNext(jump))
                {
                    return true;
                }
                else
                {
                    if (trajectoryIndex >= trajectory.trajectories.size()-1)
                    {
                        return false;
                    }
                    Iterator<Trajectory> it = trajectory.trajectories.listIterator(trajectoryIndex+1);
                    while (it.hasNext())
                    {
                        Trajectory t = it.next();
                        if (jump < t.getLength())
                        {
                            return true;
                        }
                        jump -= t.getLength();                        
                    }
                }
            }
            return false;
        }

        @Override
        public Point next() {
            if (iterator == null)
            {
                throw new NoSuchElementException();
            }
            Point point = iterator.next();
            absolutePointIndex++;            
            if (!iterator.hasNext()) {
                trajectoryIndex++;
                if (trajectoryIndex < trajectory.trajectories.size()) {
                    iterator = trajectory.trajectories.get(trajectoryIndex).iterator();                    
                } else {
                    iterator = null;
                }
            }
            return point;
        }

        @Override
        public Point next(int jump) {
            if (iterator == null)
            {
                throw new NoSuchElementException();
            }
            if (iterator.hasNext(jump))
            {
                absolutePointIndex += jump;                
                return iterator.next(jump);
            }
            else
            {
                if (trajectoryIndex >= trajectory.trajectories.size()-1)
                {
                    throw new NoSuchElementException();
                }
                int newTrajectoryIndex = trajectoryIndex + 1;                
                int newAbsolutePointIndex = absolutePointIndex += jump;
                Iterator<Trajectory> it = trajectory.trajectories.listIterator(newTrajectoryIndex);
                while (it.hasNext()) 
                {
                    Trajectory t = it.next();
                    if (jump < t.getLength())
                    {                        
                        absolutePointIndex = newAbsolutePointIndex;
                        trajectoryIndex = newTrajectoryIndex;
                        iterator = t.iterator(jump);
                        return iterator.next();
                    }
                    jump -= t.getLength();
                    newTrajectoryIndex++;
                }
                throw new NoSuchElementException();
            }
        }

        @Override
        public int getPositionOnTrajectory()
        {
            if (iterator == null)
            {
                throw new NoSuchElementException("Iterator doesn't point to any Point so it doesn't have a position.");
            }
            return absolutePointIndex;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
