package org.sybila.parasim.computation.monitoring.stl.cpu;

import static org.testng.Assert.*;
import org.testng.annotations.Test;
import org.sybila.parasim.model.trajectory.ArrayTrajectory;
import org.sybila.parasim.model.verification.stl.IntervalType;
import java.util.List;
import java.util.Iterator;

/**
 * Test for AndMonitor.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public class AndMonitorTest extends AbstractEvaluableTest<ArrayTrajectory, SimplePropertyRobustness>
{  

    @Test
    public void testEmptyness()
    {
        super.testEmptyness(getTrajectory(100,2,6.0f), new TimeInterval(2.0f, 4.0f, IntervalType.CLOSED));
    }

    @Test
    public void testBegining()
    {
        super.testBegining(getTrajectory(100,2,6.0f), new TimeInterval(2.015f, 4.0f, IntervalType.CLOSED));
    }

    @Test(enabled=false) //FIXME
    public void testEnd()
    {
        super.testEnd(getTrajectory(100,2,10.0f), new TimeInterval(2.015f, 4.056f, IntervalType.CLOSED));
        super.testEnd(getTrajectory(100,2,10.0f), new TimeInterval(2.015f, 9.0f, IntervalType.OPEN));
        super.testEnd(getTrajectory(100,2,10.0f), new TimeInterval(2.015f, 9.0f, IntervalType.CLOSED));
    }

    @Test(enabled=false) //FIXME
    public void testEvaluate()
    {
        ArrayTrajectory trajectory = getTrajectory(101, 2, 10.0f);        

        InequalityEvaluator ie1 = new InequalityEvaluator(0, 0.0f, InequalityType.LESS);
        InequalityEvaluator ie2 = new InequalityEvaluator(1, 0.0f, InequalityType.LESS);
        PredicateMonitor pred1 = new PredicateMonitor(ie1);
        PredicateMonitor pred2 = new PredicateMonitor(ie2);

        AndMonitor monitor = new AndMonitor(pred1,pred2);
        TimeInterval interval = new TimeInterval(0.0f, 10.0f, IntervalType.CLOSED);

        List<SimplePropertyRobustness> rob1 = pred1.evaluate(trajectory, interval);
        List<SimplePropertyRobustness> rob2 = pred2.evaluate(trajectory, interval);
        List<SimplePropertyRobustness> rob = monitor.evaluate(trajectory, interval);

        Iterator<SimplePropertyRobustness> it1 = rob1.iterator();
        Iterator<SimplePropertyRobustness> it2 = rob2.iterator();
        Iterator<SimplePropertyRobustness> it = rob.iterator();

        assertEquals(rob1.size(),rob2.size(),"Subexpression results have different length.");
        assertEquals(rob.size(),rob1.size(),"Subexpression result and main result have different length.");

        while (it.hasNext())
        {
            SimplePropertyRobustness pr1 = it1.next();
            SimplePropertyRobustness pr2 = it2.next();
            SimplePropertyRobustness pr = it.next();

            assertTrue(pr1.getTime() == pr2.getTime() && pr1.getTime() == pr.getTime());
            assertEquals(pr.value(),Math.min(pr1.value(), pr2.value()),
                    "The result differes for time " + pr.getTime() + " (pr1 = " + pr1.value() + ", pr2 = " + pr2.value() + ", pr = " + pr.value());
        }
    }

    @Override
    public Evaluable<ArrayTrajectory, SimplePropertyRobustness> getMonitor()
    {
        InequalityEvaluator ie1 = new InequalityEvaluator(0, 0.0f, InequalityType.LESS);
        InequalityEvaluator ie2 = new InequalityEvaluator(1, 0.0f, InequalityType.LESS);
        PredicateMonitor pred1 = new PredicateMonitor(ie1);
        PredicateMonitor pred2 = new PredicateMonitor(ie2);

        AndMonitor monitor = new AndMonitor(pred1,pred2);
        return monitor;
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
