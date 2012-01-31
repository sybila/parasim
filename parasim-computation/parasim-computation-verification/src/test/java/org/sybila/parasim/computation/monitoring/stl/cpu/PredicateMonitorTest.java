package org.sybila.parasim.computation.monitoring.stl.cpu;

import static org.testng.Assert.*;
import org.testng.annotations.Test;
import org.sybila.parasim.model.trajectory.ArrayTrajectory;
import org.sybila.parasim.model.trajectory.Point;
import java.util.List;
import java.util.Iterator;

/**
 * Test of PredicateMonitor.
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public class PredicateMonitorTest extends AbstractEvaluableTest<ArrayTrajectory, SimplePropertyRobustness>
{

    @Test
    public void testEmptyness()
    {
        super.testEmptyness(getTrajectory(100,3,6), 2.0f, 4.0f);
    }

    @Test
    public void testBegining()
    {
        super.testBegining(getTrajectory(100,3,6), 2.015f, 4.0f);
    }

    @Test
    public void testEnd()
    {
        super.testEnd(getTrajectory(100,3,6), 2.015f, 4.056f);
    }

    @Test(enabled=false)
    public void testCount()
    {
        PredicateEvaluator ie = new InequalityEvaluator(0,4.0f,InequalityType.LESS);
        Evaluable predicateMonitor = new PredicateMonitor(ie);
        ArrayTrajectory trajectory = getTrajectory(100, 2, 5.0f);
        List<SimplePropertyRobustness> rob = predicateMonitor.evaluate(trajectory, 0.0f, 5.0f);
        assertEquals(rob.size(),100);
        rob = predicateMonitor.evaluate(trajectory, 6.0f, 7.0f);
        assertEquals(rob.size(),1);
    }

    @Test(enabled=false)
    public void testValues()
    {
        PredicateEvaluator ie = new InequalityEvaluator(0,4.0f,InequalityType.LESS);
        Evaluable predicateMonitor = new PredicateMonitor(ie);
        ArrayTrajectory trajectory = getTrajectory(100, 2, 5.0f);
        List<SimplePropertyRobustness> rob = predicateMonitor.evaluate(trajectory, 1.0f, 4.5f);        
        Iterator<Point> it = trajectory.iterator();
        Point current = it.next();
        Point previous;
        while (it.hasNext())
        {
            previous = current;
            current = it.next();
            if (previous.getTime() >= 1.0f && previous.getTime() <= 4.5f)
            {
                float pr = getRobustnessValue(rob, previous.getTime()).value();
                float tmp = ie.value(previous, current).value();
                assertEquals(pr, tmp, "Values differ in time point "+previous.getTime());
            }
        }
    }

    @Override
    public Evaluable<ArrayTrajectory, SimplePropertyRobustness> getMonitor()
    {
        PredicateEvaluator ie = new InequalityEvaluator(0,4.0f,InequalityType.LESS);
        return new PredicateMonitor(ie);
    }

    @Override
    public ArrayTrajectory getTrajectory(int length, int dim, float time)
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

    @Override
    public SimplePropertyRobustness createRobustness(float time, float value, float derivative)
    {
        return new SimplePropertyRobustness(time, value, derivative);
    }

}
