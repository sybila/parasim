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

    @Test
    public void testEnd()
    {
        super.testEnd(getTrajectory(100,2,10.0f), new TimeInterval(2.015f, 4.056f, IntervalType.CLOSED));
        super.testEnd(getTrajectory(100,2,10.0f), new TimeInterval(2.015f, 9.0f, IntervalType.OPEN));
        super.testEnd(getTrajectory(100,2,10.0f), new TimeInterval(2.015f, 9.0f, IntervalType.CLOSED));
    }

    @Test
    public void testSynchronousSubexpressionValuesResult()
    {
        ArrayTrajectory trajectory = getTrajectory(11, 2, 10.0f);        

        InequalityEvaluator ie1 = new InequalityEvaluator(0, 0.0f, InequalityType.LESS);
        InequalityEvaluator ie2 = new InequalityEvaluator(1, 0.0f, InequalityType.LESS);
        PredicateMonitor pred1 = new PredicateMonitor(ie1);
        PredicateMonitor pred2 = new PredicateMonitor(ie2);

        AndMonitor monitor = new AndMonitor(pred1,pred2);
        TimeInterval interval = new TimeInterval(0.0f, 10.0f, IntervalType.CLOSED);

        List<SimplePropertyRobustness> rob1 = pred1.evaluate(trajectory, interval);
        List<SimplePropertyRobustness> rob2 = pred2.evaluate(trajectory, interval);
        List<SimplePropertyRobustness> rob = monitor.evaluate(trajectory, interval);
        
        Iterator<SimplePropertyRobustness> it = rob.iterator();        

        while (it.hasNext())
        {
            SimplePropertyRobustness pr = it.next();
            SimplePropertyRobustness pr1 = super.getRobustnessValue(rob1, pr.getTime());
            SimplePropertyRobustness pr2 = super.getRobustnessValue(rob2, pr.getTime());

            assertTrue(pr1.getTime() == pr2.getTime() && pr1.getTime() == pr.getTime(),"IT0: Points differ in time.");
            /* Since computation of intersection values in AndMonitor.evaluate()
               is different from the computation of values in a given time in
               getRobustnessValue() the following test is sensible only if the
               robustness values are sufficiently different
             */
            if (Math.abs(pr1.value() - pr2.value()) / Math.min(Math.abs(pr1.value()),Math.abs(pr2.value())) > 0.001)
            {
                assertEquals(pr.value(),Math.min(pr1.value(), pr2.value()),
                    "IT0: The result differs for time " + pr.getTime() + " (pr1 = " + pr1.value() + ", pr2 = " + pr2.value() + ", pr = " + pr.value());
            }            
        }

        Iterator<SimplePropertyRobustness> it1 = rob1.iterator();

        while (it1.hasNext())
        {
            SimplePropertyRobustness pr1 = it1.next();
            SimplePropertyRobustness pr = super.getRobustnessValue(rob, pr1.getTime());
            SimplePropertyRobustness pr2 = super.getRobustnessValue(rob2, pr1.getTime());

            assertTrue(pr1.getTime() == pr2.getTime() && pr1.getTime() == pr.getTime(),"IT1: Points differ in time.");
            /* Since computation of intersection values in AndMonitor.evaluate()
               is different from the computation of values in a given time in
               getRobustnessValue() the following test is sensible only if the
               robustness values are sufficiently different
             */
            if (Math.abs(pr1.value() - pr2.value()) / Math.min(Math.abs(pr1.value()),Math.abs(pr2.value())) > 0.001)
            {
                assertEquals(pr.value(),Math.min(pr1.value(), pr2.value()),
                    "IT1: The result differs for time " + pr.getTime() + " (pr1 = " + pr1.value() + ", pr2 = " + pr2.value() + ", pr = " + pr.value());
            }
        }

        Iterator<SimplePropertyRobustness> it2 = rob2.iterator();

        while (it1.hasNext())
        {
            SimplePropertyRobustness pr2 = it2.next();
            SimplePropertyRobustness pr = super.getRobustnessValue(rob, pr2.getTime());
            SimplePropertyRobustness pr1 = super.getRobustnessValue(rob1, pr2.getTime());

            assertTrue(pr1.getTime() == pr2.getTime() && pr1.getTime() == pr.getTime(),"IT2: Points differ in time.");
            /* Since computation of intersection values in AndMonitor.evaluate()
               is different from the computation of values in a given time in
               getRobustnessValue() the following test is sensible only if the
               robustness values are sufficiently different
             */
            if (Math.abs(pr1.value() - pr2.value()) / Math.min(Math.abs(pr1.value()),Math.abs(pr2.value())) > 0.001)
            {
                assertEquals(pr.value(),Math.min(pr1.value(), pr2.value()),
                    "IT2: The result differs for time " + pr.getTime() + " (pr1 = " + pr1.value() + ", pr2 = " + pr2.value() + ", pr = " + pr.value());
            }
        }
    }

    @Test(enabled=false)
    public void testAsynchronousSubexpressionValuesResult()
    {
        ArrayTrajectory trajectory1 = getTrajectory(41, 2, 10.0f);
        ArrayTrajectory trajectory2 = getTrajectory(31, 2, 10.0f);

        InequalityEvaluator ie1 = new InequalityEvaluator(0, 0.0f, InequalityType.LESS);
        InequalityEvaluator ie2 = new InequalityEvaluator(1, 0.0f, InequalityType.LESS);
        PredicateMonitor pred1 = new PredicateMonitor(ie1);
        PredicateMonitor pred2 = new PredicateMonitor(ie2);

        AndMonitor monitor = new AndMonitor(pred1,pred2);
        TimeInterval interval = new TimeInterval(0.0f, 10.0f, IntervalType.CLOSED);

        List<SimplePropertyRobustness> rob1 = pred1.evaluate(trajectory1, interval);
        List<SimplePropertyRobustness> rob2 = pred2.evaluate(trajectory2, interval);
        //FIXME
        List<SimplePropertyRobustness> rob = monitor.evaluate(trajectory1, interval);

        Iterator<SimplePropertyRobustness> it = rob.iterator();

        while (it.hasNext())
        {
            SimplePropertyRobustness pr = it.next();
            SimplePropertyRobustness pr1 = super.getRobustnessValue(rob1, pr.getTime());
            SimplePropertyRobustness pr2 = super.getRobustnessValue(rob2, pr.getTime());

            assertTrue(pr1.getTime() == pr2.getTime() && pr1.getTime() == pr.getTime(),"IT0: Points differ in time.");
            /* Since computation of intersection values in AndMonitor.evaluate()
               is different from the computation of values in a given time in
               getRobustnessValue() the following test is sensible only if the
               robustness values are sufficiently different
             */
            if (Math.abs(pr1.value() - pr2.value()) / Math.min(Math.abs(pr1.value()),Math.abs(pr2.value())) > 0.001)
            {
                assertEquals(pr.value(),Math.min(pr1.value(), pr2.value()),
                    "IT0: The result differs for time " + pr.getTime() + " (pr1 = " + pr1.value() + ", pr2 = " + pr2.value() + ", pr = " + pr.value());
            }
        }

        Iterator<SimplePropertyRobustness> it1 = rob1.iterator();

        while (it1.hasNext())
        {
            SimplePropertyRobustness pr1 = it1.next();
            SimplePropertyRobustness pr = super.getRobustnessValue(rob, pr1.getTime());
            SimplePropertyRobustness pr2 = super.getRobustnessValue(rob2, pr1.getTime());

            assertTrue(pr1.getTime() == pr2.getTime() && pr1.getTime() == pr.getTime(),"IT1: Points differ in time.");
            /* Since computation of intersection values in AndMonitor.evaluate()
               is different from the computation of values in a given time in
               getRobustnessValue() the following test is sensible only if the
               robustness values are sufficiently different
             */
            if (Math.abs(pr1.value() - pr2.value()) / Math.min(Math.abs(pr1.value()),Math.abs(pr2.value())) > 0.001)
            {
                assertEquals(pr.value(),Math.min(pr1.value(), pr2.value()),
                    "IT1: The result differs for time " + pr.getTime() + " (pr1 = " + pr1.value() + ", pr2 = " + pr2.value() + ", pr = " + pr.value());
            }
        }

        Iterator<SimplePropertyRobustness> it2 = rob2.iterator();

        while (it1.hasNext())
        {
            SimplePropertyRobustness pr2 = it2.next();
            SimplePropertyRobustness pr = super.getRobustnessValue(rob, pr2.getTime());
            SimplePropertyRobustness pr1 = super.getRobustnessValue(rob1, pr2.getTime());

            assertTrue(pr1.getTime() == pr2.getTime() && pr1.getTime() == pr.getTime(),"IT2: Points differ in time.");
            /* Since computation of intersection values in AndMonitor.evaluate()
               is different from the computation of values in a given time in
               getRobustnessValue() the following test is sensible only if the
               robustness values are sufficiently different
             */
            if (Math.abs(pr1.value() - pr2.value()) / Math.min(Math.abs(pr1.value()),Math.abs(pr2.value())) > 0.001)
            {
                assertEquals(pr.value(),Math.min(pr1.value(), pr2.value()),
                    "IT2: The result differs for time " + pr.getTime() + " (pr1 = " + pr1.value() + ", pr2 = " + pr2.value() + ", pr = " + pr.value());
            }
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
