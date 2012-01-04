package org.sybila.parasim.computation.monitoring.stl.cpu;

import static org.testng.Assert.*;
import org.sybila.parasim.computation.monitoring.PropertyRobustness;
import org.sybila.parasim.model.trajectory.Trajectory;
import java.util.List;
import java.util.Iterator;

/**
 * Enables testing of the Evaluable interface.
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public abstract class AbstractEvaluableTest<T extends Trajectory, R extends PropertyRobustness>
{        
    protected void testEmptyness(T trajectory, float a, float b)
    {
        List<R> result = getMonitor().evaluate(trajectory, a, b);
        assertFalse(result.isEmpty());
    }

    protected void testBegining(T trajectory, float a, float b)
    {
        List<R> result = getMonitor().evaluate(trajectory, a, b);
        assertTrue(result.get(0).getTime() == a);
    }

    protected void testEnd(T trajectory, float a, float b)
    {
        List<R> result = getMonitor().evaluate(trajectory, a, b);
        assertTrue(result.get(result.size()-1).getTime() <= b);
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
        while (it.hasNext() && current.getTime() < time)
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

    public abstract R createRobustness(float time, float value, float derivative);
}
