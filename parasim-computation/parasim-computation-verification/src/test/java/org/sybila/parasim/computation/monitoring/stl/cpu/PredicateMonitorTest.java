package org.sybila.parasim.computation.monitoring.stl.cpu;

import org.sybila.parasim.model.verification.stl.TimeInterval;
import static org.testng.Assert.*;
import org.testng.annotations.Test;
import org.sybila.parasim.model.trajectory.ArrayTrajectory;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.verification.stl.IntervalBoundaryType;
import java.util.List;
import java.util.Iterator;

/**
 * Test of PredicateMonitor.
 * 
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public class PredicateMonitorTest extends AbstractEvaluableTest<ArrayTrajectory, SimplePropertyRobustness>
{

    @Test
    public void testEmptyness()
    {
        super.testEmptyness(getTrajectory(100,3,6.0f), new TimeInterval(2.0f, 4.0f, IntervalBoundaryType.CLOSED));
    }

    @Test
    public void testBegining()
    {
        super.testBegining(getTrajectory(100,3,6.0f), new TimeInterval(2.015f, 4.0f, IntervalBoundaryType.CLOSED));
    }

    @Test
    public void testEnd()
    {
        super.testEnd(getTrajectory(100,3,10.0f), new TimeInterval(2.015f, 4.056f, IntervalBoundaryType.CLOSED));
        super.testEnd(getTrajectory(100,3,10.0f), new TimeInterval(2.015f, 9.0f, IntervalBoundaryType.OPEN));
        super.testEnd(getTrajectory(100,3,10.0f), new TimeInterval(2.015f, 9.0f, IntervalBoundaryType.CLOSED));
    }

    @Test
    public void testCount()
    {
        PredicateEvaluator ie = new InequalityEvaluator(0,4.0f,InequalityType.LESS);
        Evaluable predicateMonitor = new PredicateMonitor(ie);
        ArrayTrajectory trajectory = getTrajectory(100, 2, 10.0f);
        List<SimplePropertyRobustness> rob = predicateMonitor.evaluate(trajectory, new TimeInterval(0.0f, 10.0f, IntervalBoundaryType.OPEN));
        assertEquals(rob.size(),99);
        trajectory = getTrajectory(11, 2, 10.0f);
        rob = predicateMonitor.evaluate(trajectory, new TimeInterval(0.0f, 10.0f, IntervalBoundaryType.CLOSED));
        assertEquals(rob.size(),10);
        rob = predicateMonitor.evaluate(trajectory, new TimeInterval(0.0f, 5.0f, IntervalBoundaryType.OPEN));
        assertEquals(rob.size(),5);
        rob = predicateMonitor.evaluate(trajectory, new TimeInterval(0.0f, 5.0f, IntervalBoundaryType.CLOSED));
        assertEquals(rob.size(),6);
    }

    @Test
    public void testValues()
    {
        PredicateEvaluator ie = new InequalityEvaluator(0,4.0f,InequalityType.LESS);
        Evaluable predicateMonitor = new PredicateMonitor(ie);
        ArrayTrajectory trajectory = getTrajectory(101, 2, 5.0f);
        List<SimplePropertyRobustness> rob = predicateMonitor.evaluate(trajectory, new TimeInterval(1.0f, 4.5f, IntervalBoundaryType.CLOSED));
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
        return getArrayTrajectory(length, dim, time);
    }

    @Override
    public SimplePropertyRobustness createRobustness(float time, float value, float derivative)
    {
        return new SimplePropertyRobustness(time, value, derivative);
    }

}
