/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim.computation.simulation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A base trajectory is expected to be 
 * @author Sven Dražan <sven@mail.muni.cz>
 */
public class BaseTrajectory implements Trajectory {

    /** First point of the trajectory. */
    private Point initialPoint;    
    /** List of trajectory segments which are subtrajectories. */
    private ArrayList<Trajectory> segments;
    /** Number of points in all segments together. */
    private int length;

    BaseTrajectory(Point initialPoint)
    {
        this.initialPoint = initialPoint;
        length = 1;
        segments = new ArrayList<Trajectory>();
    }

    public int getDimension()
    {
        return initialPoint.getDimension();
    }

    public Point getLastPoint()
    {
        if (length == 1) {
            return initialPoint;
        }
        else
        {
            return segments.get(segments.size()-1).getLastPoint();
        }
    }

    public int getLength()
    {
        return length;
    }

    public TrajectoryIterator iterator(int index)
    {
        if ( !hasPoint(index) )
        {
            throw new ArrayIndexOutOfBoundsException("index ("+index+
                    ") must be in ["+getFirstPointIndex()+", "+
                    getLastPoint().getPositionOnTrajectory()+"]");
        }
        return new BaseTrajectoryIterator(index);
    }
    public TrajectoryIterator iterator()
    {
        return new BaseTrajectoryIterator(initialPoint.getPositionOnTrajectory());
    }

    public int getFirstPointIndex()
    {
        return initialPoint.getPositionOnTrajectory();
    }

    public boolean hasPoint(int index)
    {
        return (index >= initialPoint.getPositionOnTrajectory() &&
                index <= getLastPoint().getPositionOnTrajectory() );
    }

    public Point getPoint(int index)
    {
        if ( !hasPoint(index) )
        {
            throw new ArrayIndexOutOfBoundsException("index ("+index+
                    ") must be in ["+getFirstPointIndex()+", "+
                    getLastPoint().getPositionOnTrajectory()+"]");
        }
        if (index == initialPoint.getPositionOnTrajectory())
        {
            return initialPoint;
        }
        Iterator<Trajectory> it = segments.iterator();
        while (it.hasNext())
        {
            Trajectory segment = it.next();
            if (segment.hasPoint(index))
            {
                return segment.getPoint(index);
            }
        }
        throw new ArrayIndexOutOfBoundsException("This should not happen.");
    }

    public boolean append(Trajectory trajectory)
    {
        if ( trajectory.getFirstPointIndex() != getLastPoint().getPositionOnTrajectory() + 1 )
        {
            return false;
        }
        else
        {
            segments.add(trajectory);
            length += trajectory.getLength();
            return true;
        }
    }

    private class BaseTrajectoryIterator implements TrajectoryIterator {

        /** iterator over the list of segments */
        private Iterator<Trajectory> listIterator;
        /** iterator inside each segment */
        private TrajectoryIterator segmentIterator;
        /** index of next() point */
        private int pointIndex;

        /**
         * Creates a new BaseTrajectoryIterator.
         * @param pointIndex The index of the point returned in the first
         *        call to the next() method.
         */
        BaseTrajectoryIterator(int pointIndex)
        {
            listIterator = segments.iterator();
            if (pointIndex != initialPoint.getPositionOnTrajectory())
            {
                while ( listIterator.hasNext() )
                {
                    Trajectory segment = listIterator.next();
                    if ( segment.hasPoint(pointIndex) )
                    {
                        segmentIterator = segment.iterator(pointIndex);
                        break;
                    }
                }                
            }
            this.pointIndex = pointIndex-1;
        }

        public boolean hasNext()
        {
            return ( pointIndex + 1 <= getLastPoint().getPositionOnTrajectory() );
        }

        public boolean hasNext(int jump)
        {
            return ( pointIndex + jump <= getLastPoint().getPositionOnTrajectory() );
        }

        public Point next()
        {
            if ( (pointIndex + 1) == initialPoint.getPositionOnTrajectory() )
            {
                if ( listIterator.hasNext() )
                {
                    segmentIterator = listIterator.next().iterator();
                }                
                pointIndex++;
                return initialPoint;
            }
            else
            {
                if ( segmentIterator.hasNext() )
                {
                    pointIndex++;
                    return segmentIterator.next();
                }
                else
                {
                    if ( listIterator.hasNext() )
                    {
                        segmentIterator = listIterator.next().iterator();
                        pointIndex++;
                        return segmentIterator.next();
                    }
                    else
                    {
                        throw new NoSuchElementException("next Point doesn't exist");
                    }
                }
            }
        }

        public Point next(int jump)
        {
            if ( (pointIndex + 1) == initialPoint.getPositionOnTrajectory() )
            {
                if (jump == 1)
                {
                    pointIndex++;
                    if ( listIterator.hasNext() )
                    {
                        segmentIterator = listIterator.next().iterator();
                    }
                    return initialPoint;
                }
                else
                {
                    while ( listIterator.hasNext() )
                    {
                        Trajectory segment = listIterator.next();
                        if ( segment.hasPoint(pointIndex + jump) )
                        {
                            pointIndex += jump;
                            segmentIterator = segment.iterator(pointIndex);
                            return segmentIterator.next();
                        }
                    }
                }
                throw new NoSuchElementException("next Point doesn't exist");
            }
            else
            {
                if ( segmentIterator.hasNext(jump) )
                {
                    pointIndex += jump;
                    return segmentIterator.next(jump);
                }
                else
                {
                    while ( listIterator.hasNext() )
                    {
                        Trajectory segment = listIterator.next();
                        if ( segment.hasPoint(pointIndex + jump) )
                        {
                            pointIndex += jump;
                            segmentIterator = segment.iterator(pointIndex);
                            return segmentIterator.next();
                        }
                    }
                    throw new NoSuchElementException("next Point doesn't exist");
                }
            }
        }

        public void remove()
        {
            throw new UnsupportedOperationException("remove() not supported in BaseTrajectoryIterator");
        }

    }

}
