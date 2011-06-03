
package parasim.computation.simulation;

import java.util.NoSuchElementException;

/**
 * Trajectories are simulated in blocks that are continuous sequences of
 * points. Because many of the types of computation also operate on sequences
 * of points, we have decided to keep the data closely together on this level.
 *
 * @author sven
 */
public class TrajectorySegment implements Trajectory {

    /** Position of TrajectorySegment in whole Trajectory, 0 is the first.
        This is also the index of the first point of the segment */
    private int firstPointIndex;
    /** Raw data as sequence of all points one after another. */
    private float[] points;
    /** Number of points in TrajectorySegment */
    private int length;
    /** Dimension of a single point. */
    private int dimension;

    TrajectorySegment(float[] points, int length, int dimension, int firstPointIndex)
    {
        if (length <= 0 || dimension <= 0 || firstPointIndex < 0 ||
            points.length != (length * dimension) )
        {
            throw new IllegalArgumentException("Bad parameters [ points(" +
                    points.length + "), " + length + ", " + dimension +
                    ", " + firstPointIndex);
        }
        this.points = points;
        this.length = length;
        this.dimension = dimension;
        this.firstPointIndex = firstPointIndex;
    }

    public Point getLastPoint()
    {
        return new PointHandle(points, (length-1)*dimension, dimension, firstPointIndex+length-1);
    }

    /*
    public Point getFirstPoint()
    {
        return new PointHandle(points, 0, pointDimension, firstPointIndex);
    }*/

    /**
     * @return Index of the first point of the segment.
     */
    public int getFirstPointIndex()
    {
        return firstPointIndex;
    }

    public int getLength()
    {
        return length;
    }
    
    /**
     * Returns the Point with given index if in array.
     * The first point's index is equal to firstPointIndex.
     *
     * @param index firstPointIndex of point on trajectory
     * @return
     */
    public Point getPoint(int index)
    {
        if ( !hasPoint(index) )
        {
            throw new ArrayIndexOutOfBoundsException("Bad point index [ "+index+" ] must be in ("+firstPointIndex+", "+(firstPointIndex+length)+")");
        }
        return new PointHandle(points, (index-firstPointIndex)*dimension, dimension, index);
    }

    public boolean hasPoint(int index)
    {
        if (firstPointIndex > index || index >= firstPointIndex+length )
        {
            return false;
        }
        else return true;
    }

    public boolean append(Trajectory trajectory)
    {
        throw new UnsupportedOperationException("append() not supported");
    }

    public TrajectoryIterator iterator()
    {
        return new TrajectorySegmentIterator(firstPointIndex);
    }

    /**
     * @param index Index of the point which the iterator will return on next().
     * @return Iterator on success.
     */
    public TrajectoryIterator iterator(int index)
    {
        if ( !hasPoint(index) )
        {
            throw new ArrayIndexOutOfBoundsException("Bad point index [ "+index+" ] must be in ("+firstPointIndex+", "+(firstPointIndex+length)+")");
        }
        return new TrajectorySegmentIterator(index);
    }

    /**
     * @return the pointDimension
     */
    public int getDimension() {
        return dimension;
    }

    private class TrajectorySegmentIterator implements TrajectoryIterator {
        
        private int pointIndex;

        /**         
         * @param segments ArrayList of TrajectoryBlocks
         * @param segmentIndex The index of the block in which the next point resides.
         * @param pointIndex The index of the next point in the current block.
         */
        TrajectorySegmentIterator(int pointIndex)
        {            
            this.pointIndex = pointIndex-1;
        }

        public boolean hasNext()
        {
            return ( pointIndex + 1 < firstPointIndex + length );
        }

        /**
         * Determines whether there exists a next point jump away.
         * In case jump = 1 this method is the same as hasNext().
         * @param jump jump to next point
         * @return true if point exists, false otherwise
         */
        public boolean hasNext(int jump)
        {
            return ( pointIndex + jump < firstPointIndex + length );
        }

        public Point next()
        {
            if ( hasNext() )
            {
                pointIndex++;
                return new PointHandle(points, (pointIndex-firstPointIndex)*dimension, dimension, pointIndex);
            }
            else
            {
                throw new NoSuchElementException("next Point doesn't exist");
            }    
        }

        public Point next(int jump)
        {
            if ( hasNext(jump) )
            {
                pointIndex += jump;
                return new PointHandle(points, (pointIndex-firstPointIndex)*dimension, dimension, pointIndex);
            }
            else
            {
                throw new NoSuchElementException("next Point doesn't exist");
            }
        }

        public void remove()
        {
            throw new UnsupportedOperationException("remove() not supported in TrajectorySegmentIterator");
        }

    }

}
