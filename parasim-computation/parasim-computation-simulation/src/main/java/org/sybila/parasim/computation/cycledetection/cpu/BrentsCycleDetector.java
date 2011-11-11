package org.sybila.parasim.computation.cycledetection.cpu;

import org.sybila.parasim.computation.cycledetection.CycleDetector;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.trajectory.TrajectoryIterator;
import org.sybila.parasim.model.trajectory.PointComparator;

/**
 * Implements Brent's cycle detection algorithm.
 * 
 * It's main advantage is that it uses a constant amount of memory, so it is
 * suitable for implementation on GPUs which have a thread memory limit.
 *
 * The cycle detector is guaranteed to find a cycle with minimal length
 * but does not guarantee to find the minimal loop back point.
 *
 * Code has been adopted from these sources:
 * (<a href="http://en.wikipedia.org/wiki/Cycle_detection#Brent.27s_algorithm">Wikipedia</a>,
 * <a href="http://www.siafoo.net/algorithm/11">SiafOO</a>)
 * 
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public class BrentsCycleDetector<T extends Trajectory> implements CycleDetector<T>
{
    private Point turtle;
    private Point rabbit;
    private int stepsTaken;
    private int stepLimit;
    private boolean cycleDetected;
    private PointComparator comparator;
    /** Index of the next point on the trajectory to be processed. */
    private int nextPointIndex;

    BrentsCycleDetector(PointComparator comparator)
    {        
        if (comparator == null)
        {
            throw new IllegalArgumentException("The parameter comparator is null.");
        }        
        this.comparator = comparator;
        stepsTaken = 0;
        stepLimit = 2;
        nextPointIndex = -1;
        cycleDetected = false;
    }

    @Override
    public int detectCycle(T trajectory, int workLimit)
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
        if (workLimit == 0)
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
            while (stepsUsed < workLimit && iterator.hasNext())
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
     * Detects if the newPoint is not similar to a previously tested point.
     *
     * After a cycle is detected to compute the minimal start of the cycle
     * minimizeCycle must be called. This costs one more iteration through
     * the trajectory.
     *
     * @param newPoint Point to be tested for similarity with previous points.
     * @return True if similarity is found, false else.
     */    
    private boolean detectCycle(Point newPoint)
    {        
        if (turtle == null) /* First input = initialization */
        {
            turtle = newPoint;
            rabbit = newPoint;
            return false;
        }

        rabbit = newPoint;
        stepsTaken += 1;

        if (comparator.similar(turtle, rabbit))
        {
            cycleDetected = true;
            return true;       
        }

        if (stepsTaken == stepLimit)
        {
            stepsTaken = 0;
            stepLimit *= 2;
            turtle = rabbit; /* teleport the turtle */
        }
        return false;
    }

    @Override
    public Point getCycleStart()
    {
        if (cycleDetected)
        {
            return turtle;
        }
        return null;
    }

    @Override
    public Point getCycleEnd()
    {
        if (cycleDetected)
        {
            return rabbit;
        }
        return null;
    }

    /**
     * After a cycle has been detected a call to this method finds the first
     * occurence of the period thus minimizing the non-periodic prefix and moving
     * both points in which the cycle has been detected closer to the begining
     * of the trajectory. If a cycle has not been detected or the cycle
     * is already minimal nothing changes.
     * 
     * @param trajectory Trajectory on which to perform cycle minimization.
     */
    public void minimizeCycle(T trajectory)
    {
        if (!cycleDetected) return;
        TrajectoryIterator iTurtle = trajectory.iterator();
        TrajectoryIterator iRabbit = trajectory.iterator(stepsTaken);
        Point newRabbit, newTurtle;

        while (iRabbit.hasNext())
        {
            newRabbit = iRabbit.next();
            newTurtle = iTurtle.next();
            if (comparator.similar(newTurtle, newRabbit))
            {
                turtle = newTurtle;
                rabbit = newRabbit;
            }
        }
    }

    /**
     * If a cycle has been detected it's length is returned. Else -1 is returned.
     * @return Length of a detected cycle if it has been found or -1.
     */
    public int getCycleLength()
    {
        if (cycleDetected)
        {
            return stepsTaken;
        }
        else
        {
            return -1;
        }
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
