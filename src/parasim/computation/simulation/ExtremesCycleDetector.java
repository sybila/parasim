
package parasim.computation.simulation;

import parasim.computation.Point;

/**
 * The ExtremesCycleDetector provides means to detect cycles on trajectories.
 * A cycle is considered the repetition of points who's values are close enough
 * on all dimensions but the time dimension. Close enough is specified by the
 * relative tolerance same for all dimensions or the absolute tolerance
 * possibly different for each dimension.
 *
 * To detect a cycle the extrems of the trajectory (minima, maxima) are
 * first detected as canonical point to compare. This works only if the
 * minima and maxima points are actualy provided to the detectCycle method
 * with a small enough tolerance.
 *
 * Mode specifies if the cycle is to be detected in minima, maxima or both on
 * each dimension.
 *
 * For each dimension a queue is maintained holding the last detected extreme
 * points on this dimension and the next time a new extreme is detected
 * for this dimension the new point is compared to all the points in the queue.
 * If a cycle is not found the new point is then inserted into the queue.
 *
 * Since the queues have a limited capacity given during initialization, once
 * they are filled the oldest points are discarded.
 *
 * @author Sven Dražan <sven@mail.muni.cz>
 */
public class ExtremesCycleDetector implements CycleDetector {

    private ExtremesMode[] mode;
    private float relTolerance;
    private float[] absTolerance;
    private ExtremesQueue[] dimsQueue;
    
    /**
     * Creates a new CycleDetector with given mode, relative tolerance, capacity
     * and number of dimensions.
     *
     * @param mode
     * @param relTolerance Relative tolerance of distance between points to
     *        detect them as being one and thus completing a cycle.
     * @param capacity Number of points the detector holds at most in every
     *        dimension to compare with new points.
     * @param dimension Number of dimensions. Since the detector works only
     *        for the spatial dimensions, it is expected that dimensions >=2.
     */
    ExtremesCycleDetector(ExtremesMode mode, float relTolerance, int capacity, int dimension)
    {
        if (capacity < 1)
        {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        if (dimension < 2)
        {
            throw new IllegalArgumentException("Dimension must be >= 2");
        }
        dimension--;
        this.mode = new ExtremesMode[dimension];
        for (int i=0; i<dimension; i++)
        {
            this.mode[i] = mode;
        }
        this.relTolerance = relTolerance;
        absTolerance = new float[dimension];
        for (int i=0; i<dimension; i++)
        {
            this.absTolerance[i] = 0;
        }
        dimsQueue = new ExtremesQueue[dimension];
        for (int i=0; i<dimension; i++)
        {
            dimsQueue[i] = new ExtremesQueue(capacity);
        }
    }

    public Point detectCycle(Point newPoint)
    {
        throw new IllegalArgumentException("FIXME");
        //FIXME
    }

    /**
     * Returns the detection mode for dimension index.
     * Since the time dimension 0 does not have detection the correct index
     * muse hold: 1 <= index < dimension.
     * @param index
     * @return
     */
    ExtremesMode getMode(int index)
    {
        if (index < 1 || index > mode.length)
        {
            throw new IllegalArgumentException("Only dimensions 2.."+
                    (mode.length-1)+" have detection modes, ["+index+"]");
        }
        return mode[index-1];
    }

    void setMode(ExtremesMode mode, int index)
    {
        if (index < 1 || index > this.mode.length)
        {
            throw new IllegalArgumentException("Only dimensions 2.."+
                    (this.mode.length-1)+" have detection modes, ["+index+"]");
        }
        this.mode[index-1] = mode;
    }

    float getRelTolerance()
    {
        return relTolerance;
    }

    void setRelTolerance(float relTolerance)
    {
        this.relTolerance = relTolerance;
    }

    float getAbsTolerance(int index)
    {
        if (index < 1 || index > mode.length)
        {
            throw new IllegalArgumentException("Only dimensions 2.."+
                    (mode.length-1)+" have absolute tolerance, ["+index+"]");
        }
        return absTolerance[index-1];
    }

    void setAbsTolerance(float absTolerance, int index)
    {
        if (index < 1 || index > mode.length)
        {
            throw new IllegalArgumentException("Only dimensions 2.."+
                    (mode.length-1)+" have absolute tolerance, ["+index+"]");
        }
        this.absTolerance[index-1] = absTolerance;
    }

    int getDimension()
    {
        return mode.length+1;
    }

}
