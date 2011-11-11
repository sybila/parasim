package org.sybila.parasim.computation.cycledetection.cpu;

import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.PointComparator;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.trajectory.TrajectoryIterator;
import java.util.Iterator;
import org.sybila.parasim.computation.cycledetection.CycleDetector;

/**
 * The ExtremesCycleDetector provides means to detect cycles on trajectories.
 * A cycle is considered the repetition of points who's values are close enough
 * on all spatial dimensions. Close enough is specified by relative tolerance
 * which is the same for all dimensions or absolute tolerance possibly
 * different for each dimension.
 *
 * To detect a cycle the extremes of the trajectory (minima, maxima) are
 * first detected as canonical points to compare. This works only if the
 * points are actualy provided to the detectCycle method with a small enough
 * tolerance. The minima and maxima are detected automaticaly from the last
 * two tested points.
 *
 * The mode array specifies if the cycle is to be detected in minima, maxima or
 * both on each dimension.
 *
 * For each dimension a queue is maintained holding the last detected extreme
 * points on this dimension and the next time a new extreme is detected
 * for this dimension the new point is compared to all the points in the queue.
 * If a cycle is not found the new point is then inserted into the queue.
 *
 * Since the queues have a limited capacity given during initialization, once
 * they are filled the oldest points are overwritten.
 *
 * Different criteria for point comparison are carried out by using the
 * RAPointComparator which is itself final, however since it is public, it's
 * setter methods are accessible.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public class ExtremesCycleDetector<T extends Trajectory> implements CycleDetector<T>
{
    private ExtremesMode[] mode;
    private PointComparator comparator;
    private ExtremesQueue[] minQueue;
    private ExtremesQueue[] maxQueue;
    /** p1 is the last point, p2 is the one before the last, these points
        are compared to the new one to detect minima and maxima */
    private Point p1, p2;
    private int nextPointIndex;
    private boolean cycleDetected;
    private Point cycleStart;
    private Point cycleEnd;

    /**
     * Private constructor to control and initialize basic variables.
     * @param capacity Number of points the detector holds at most in every
     *        dimension to compare with new points.
     * @param comparator Point comparator used to test points for similarity
     *        during cycle detection.
     */
    private ExtremesCycleDetector(int capacity, PointComparator comparator)
    {
        if (capacity < 1)
        {
            throw new IllegalArgumentException("Capacity must be positive.");
        }
        if (comparator == null)
        {
            throw new IllegalArgumentException("Parameter comparator is null.");
        }
        if (comparator.getDimension() < 1)
        {
            throw new IllegalArgumentException("Comparator dimension must be positive.");
        }
        this.comparator = comparator;
        minQueue = new ExtremesQueue[comparator.getDimension()];
        maxQueue = new ExtremesQueue[comparator.getDimension()];
        cycleDetected = false;
        nextPointIndex = -1;
    }

    /**
     * Creates a new CycleDetector with given mode for each dimension,
     * relative tolerance, absolute tolerance, capacity and number of dimensions.
     *     
     * @param capacity Number of points the detector holds at most in every
     *        dimension to compare with new points.
     * @param comparator Point comparator used to test points for similarity
     *        during cycle detection.
     * @param modes Modes of detection for each dimension.
     */
    public ExtremesCycleDetector(int capacity, PointComparator comparator, ExtremesMode modes[])
    {
        this(capacity, comparator);
        if (modes == null || modes.length != comparator.getDimension())
        {
            throw new IllegalArgumentException("modes array must have [dimension] elements");
        }        
        this.mode = modes;        
        for (int i=0; i<comparator.getDimension(); i++)
        {
            if (mode[i] == ExtremesMode.EXTREME_MIN || mode[i] == ExtremesMode.EXTREME_BOTH)
            {
                minQueue[i] = new ExtremesQueue(capacity);
            }
            if (mode[i] == ExtremesMode.EXTREME_MAX || mode[i] == ExtremesMode.EXTREME_BOTH)
            {
                maxQueue[i] = new ExtremesQueue(capacity);
            }
        }
    }

    /**
     * Creates a new CycleDetector with given mode, relative tolerance,
     * absolute tolerance, capacity and number of dimensions.
     *     
     * @param capacity Number of points the detector holds at most in every
     *        dimension to compare with new points.
     * @param comparator Point comparator used to test points for similarity
     *        during cycle detection.
     * @param mode Detection mode to use for all dimensions.
     */
    public ExtremesCycleDetector(int capacity, PointComparator comparator, ExtremesMode mode)
    {
        this(capacity, comparator);
        
        for (int i=0; i<comparator.getDimension(); i++)
        {
            if (mode == ExtremesMode.EXTREME_MIN || mode == ExtremesMode.EXTREME_BOTH)
            {
                minQueue[i] = new ExtremesQueue(capacity);
            }
            if (mode == ExtremesMode.EXTREME_MAX || mode == ExtremesMode.EXTREME_BOTH)
            {
                maxQueue[i] = new ExtremesQueue(capacity);
            }
        }
    }    

    @Override
    public int detectCycle(T trajectory, int stepLimit)
    {
        if (cycleDetected)
        {
            return 0;
        }
        TrajectoryIterator iterator;
        if (nextPointIndex == -1)
        {
            iterator = trajectory.iterator();
        }
        else
        {
            iterator = trajectory.iterator(nextPointIndex);
        }
        Point p;
        int stepsUsed = 0;
        if (stepLimit == 0)
        {
            while (iterator.hasNext())
            {
                p = iterator.next();
                stepsUsed++;
                if (detectCycle(p))
                {
                    return stepsUsed;
                }
            }
        }
        else
        {
            while (stepsUsed < stepLimit && iterator.hasNext())
            {
                p = iterator.next();
                stepsUsed++;
                if (detectCycle(p))
                {
                    return stepsUsed;
                }
            }
        }
        nextPointIndex = iterator.getPositionOnTrajectory();
        return stepsUsed;
    }

    /**
     * If the newPoint is detected as a minimum or maximum for some dimension
     * it is compared to all the points in the queue. If a similar point is
     * found it is returned, else the newPoint is inserted into the queue.
     *
     * @param newPoint new point to compare to past extreme points
     * @return last similar point to newPoint on success else null
     */    
    private boolean detectCycle(Point newPoint)
    {
        /*
        if (newPoint == null)
        {
            throw new IllegalArgumentException("newPoint is null");
        }
        if (newPoint.getDimension() != getDimension())
        {
            throw new IllegalArgumentException("newPoint differs in dimension");
        }*/
        if (p1 == null)
        {
            p1 = newPoint;
            return false;
        }
        if (p2 == null)
        {
            p2 = p1;
            p1 = newPoint;
            return false;
        }
        Point tmp = null;
        for (int i=0; i<getDimension(); i++)
        {
            if (minQueue[i] != null &&
                p2.getValue(i) > p1.getValue(i) &&
                p1.getValue(i) < newPoint.getValue(i))
            { /* p1 has been detected as a minimum */
                tmp = findSimilar(p1, minQueue[i]);
                if (tmp != null) 
                {
                    cycleStart = tmp;
                    cycleEnd = newPoint;
                    cycleDetected = true;
                    return true;
                }
                else
                {
                    minQueue[i].add(p1);
                }
            }

            if (maxQueue[i] != null &&
                p2.getValue(i) < p1.getValue(i) &&
                p1.getValue(i) > newPoint.getValue(i))
            { /* p1 has been detected as a maximum */
                tmp = findSimilar(p1, maxQueue[i]);
                if (tmp != null) 
                {
                    cycleStart = tmp;
                    cycleEnd = newPoint;
                    cycleDetected = true;
                    return true;
                }
                else
                {
                    maxQueue[i].add(p1);
                }
            }
        }
        p2 = p1;
        p1 = newPoint;
        return false;
    }

    private Point findSimilar(Point p, ExtremesQueue q)
    {
        Iterator it = q.iterator();
        while (it.hasNext())
        {
            Point tmp =(Point) it.next();
            if (comparator.similar(p, tmp))
            {
                return tmp;
            }            
        }
        return null;
    }
    
    ExtremesMode getMode(int index)
    {
        if (index < 0 || index >= mode.length)
        {
            throw new IllegalArgumentException("index must be in range [0, "+
                    (mode.length-1)+"], is "+index);
        }
        return mode[index];
    }    

    private int getDimension()
    {
        return mode.length;
    }

    @Override
    public Point getCycleStart()
    {
        if (cycleDetected)
        {
            return cycleStart;
        }
        return null;
    }

    @Override
    public Point getCycleEnd()
    {
        if (cycleDetected)
        {
            return cycleEnd;
        }
        return null;
    }

    /**
     * Returns true if a cycle has been detected, false otherwise.
     * @return True if a cycle has been detected, false otherwise.
     */
    @Override
    public boolean cycleDetected()
    {
        return cycleDetected;
    }

}

