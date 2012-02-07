package org.sybila.parasim.computation.monitoring.stl.cpu;

import static org.testng.Assert.*;
import org.sybila.parasim.computation.monitoring.PropertyRobustness;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.trajectory.ArrayTrajectory;
import org.sybila.parasim.model.verification.stl.IntervalType;
import java.util.List;
import java.util.Iterator;

/**
 * Enables testing of the Evaluable interface.
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public abstract class AbstractEvaluableTest<T extends Trajectory, R extends PropertyRobustness>
{        
    protected void testEmptyness(T trajectory, TimeInterval interval)
    {
        List<R> result = getMonitor().evaluate(trajectory, interval);
        assertFalse(result.isEmpty(),"Result is empty.");
    }

    protected void testBegining(T trajectory, TimeInterval interval)
    {
        List<R> result = getMonitor().evaluate(trajectory, interval);
        assertTrue(result.get(0).getTime() == interval.getLowerBound(),"Result begining does not match interval begining.");
    }

    protected void testEnd(T trajectory, TimeInterval interval)
    {
        List<R> result = getMonitor().evaluate(trajectory, interval);
        if (interval.getUpperType() == IntervalType.CLOSED)
        {
            assertTrue(result.get(result.size()-1).getTime() < interval.getUpperBound(),
                    "The result's last point was >= the upperBound of a CLOSED interval.");
        }
        else
        {
            assertTrue(result.get(result.size()-1).getTime() <= interval.getUpperBound(),
                    "The result's last point was > then the upperBound of a OPENED interval.");
        }
    }

    protected R getRobustnessValue(List<R> signal, float time)
    {
        if (signal == null)
        {
            throw new IllegalArgumentException("Parameter signal is null.");
        }
        if (signal.isEmpty())
        {
            throw new IllegalArgumentException("Parameter signal is empty.");
        }
        if (time < 0)
        {
            throw new IllegalArgumentException("Parameter time must be >= 0.");
        }
        Iterator<R> it = signal.iterator();
        R current = it.next();
        R last = current;
        while (it.hasNext() && current.getTime() <= time)
        {
            last = current;
            current = it.next();
        }
        return createRobustness(time, last.value() + last.getValueDerivative() * (time - last.getTime()), last.getValueDerivative());
    }

    public abstract Evaluable<T,R> getMonitor();

    /**
     * Creates a trajectory with given length, dimension and total time.
     * @param length length of trajectory to return
     * @param dim dimension of trajectory to return
     * @param time length of trajectory in time (time of last point)
     * @return trajectory with given parameters
     */
    public abstract T getTrajectory(int length, int dim, float time);

    /**
     * Creates a  <code>dim</code> dimensional trajectory over the time interval
     * [0,<code>time</code>] consisting of <code>length</code> points with values
     * given by sinusiod functions with a different period for each dimension
     * and amplitude of 10.
     *
     * @param length Number of points that will be output.
     * @param dim Number of dimensions of trajectory.
     * @param time Total time span.
     * @return Trajectory with given parameters.
     */
    public ArrayTrajectory getArrayTrajectory(int length, int dim, float time)
    {
        if (dim < 1)
        {
            throw new IllegalArgumentException("Parameter dim must be >= 1.");
        }
        if (length < 2)
        {
            throw new IllegalArgumentException("Parameter length must be >= 2.");
        }
        float[] points = new float[length * dim];
        float[] times = new float[length];

        for (int i=0; i<length; i++)
        {
            for (int d=0; d<dim; d++)
            {
                points[dim * i] = 10.0f * (float)Math.sin((d+2.0f)*(Math.PI/length)*i);
            }
            times[i] = i * (time/(length-1));
        }

        return new ArrayTrajectory(points, times, dim);
    }

    public abstract R createRobustness(float time, float value, float derivative);
}
