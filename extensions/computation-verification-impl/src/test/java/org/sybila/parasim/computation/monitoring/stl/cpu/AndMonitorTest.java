/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.computation.monitoring.stl.cpu;

import org.sybila.parasim.model.verification.stl.TimeInterval;
import static org.testng.Assert.*;
import org.testng.annotations.Test;
import org.sybila.parasim.model.trajectory.ArrayTrajectory;
import org.sybila.parasim.model.verification.stl.IntervalBoundaryType;
import java.util.List;
import java.util.TreeSet;
import java.util.Iterator;

/**
 * Test for AndMonitor.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public class AndMonitorTest extends AbstractEvaluableTest<ArrayTrajectory, SimplePropertyRobustness>
{
    /**
     * Since computation of intersection values in AndMonitor.evaluate()
     * is different from the computation of values in a given time in
     * AbstractEvaluableTest.getRobustnessValue() the equality test is
     * sensible only for sufficiently large errorTolerances.
     */
    private static final float errorTolerance = 0.0001f;

    @Test
    public void testEmptyness()
    {
        super.testEmptyness(getTrajectory(100,2,6.0f), new TimeInterval(2.0f, 4.0f, IntervalBoundaryType.CLOSED));
    }

    @Test
    public void testBegining()
    {
        super.testBegining(getTrajectory(100,2,6.0f), new TimeInterval(2.015f, 4.0f, IntervalBoundaryType.CLOSED));
    }

    @Test
    public void testEnd()
    {
        super.testEnd(getTrajectory(100,2,10.0f), new TimeInterval(2.015f, 4.056f, IntervalBoundaryType.CLOSED));
        super.testEnd(getTrajectory(100,2,10.0f), new TimeInterval(2.015f, 9.0f, IntervalBoundaryType.OPEN));
        super.testEnd(getTrajectory(100,2,10.0f), new TimeInterval(2.015f, 9.0f, IntervalBoundaryType.CLOSED));
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
        TimeInterval interval = new TimeInterval(0.0f, 10.0f, IntervalBoundaryType.CLOSED);

        List<SimplePropertyRobustness> rob1 = pred1.evaluate(trajectory, interval);
        List<SimplePropertyRobustness> rob2 = pred2.evaluate(trajectory, interval);
        List<SimplePropertyRobustness> rob = monitor.evaluate(trajectory, interval);               

        /* Collect all time points in which values should be checked. */
        TreeSet<Float> controlTimes = new TreeSet();
        Iterator<SimplePropertyRobustness> it = rob.iterator();
        while (it.hasNext())
        {
            controlTimes.add(it.next().getTime());
        }
        it = rob1.iterator();
        while (it.hasNext())
        {
            controlTimes.add(it.next().getTime());
        }
        it = rob2.iterator();
        while (it.hasNext())
        {
            controlTimes.add(it.next().getTime());
        }

        Iterator<Float> tit = controlTimes.iterator();
        while (tit.hasNext())
        {
            float time = tit.next();
            SimplePropertyRobustness pr = super.getRobustnessValue(rob, time);
            SimplePropertyRobustness pr1 = super.getRobustnessValue(rob1, time);
            SimplePropertyRobustness pr2 = super.getRobustnessValue(rob2, time);

            assertTrue(pr.getTime() == pr1.getTime() && pr.getTime() == pr2.getTime(),"IT0: Points differ in time.");            
            assertEquals(pr.value(), Math.min(pr1.value(), pr2.value()), errorTolerance,
                    "IT0: The result differs for time " + pr.getTime() + 
                    " (pr1 = " + pr1.value() + ", pr2 = " + pr2.value() +
                    ", pr = " + pr.value() + ")");
            if (pr1.value() == pr2.value())
            {
                assertEquals(pr.getValueDerivative(), Math.min(pr1.getValueDerivative(), pr2.getValueDerivative()), errorTolerance,
                    "IT0: The result derivatives differ for time " + pr.getTime() + 
                    " (Dpr1 = " + pr1.getValueDerivative() + ", Dpr2 = " + pr2.getValueDerivative() +
                    ", Dpr = " + pr.getValueDerivative() + ")");
            }
        }        
    }

    @Test
    public void testAsynchronousSubexpressionValuesResult()
    {
        ArrayTrajectory trajectory1 = getTrajectory(41, 2, 10.0f);
        ArrayTrajectory trajectory2 = getTrajectory(31, 2, 10.0f);

        InequalityEvaluator ie1 = new InequalityEvaluator(0, 0.0f, InequalityType.LESS);
        InequalityEvaluator ie2 = new InequalityEvaluator(1, 0.0f, InequalityType.LESS);
        PredicateMonitor pred1 = new PredicateMonitor(ie1);
        PredicateMonitor pred2 = new PredicateMonitor(ie2);

        AndMonitor monitor = new AndMonitor(pred1,pred2);
        TimeInterval interval = new TimeInterval(0.0f, 10.0f, IntervalBoundaryType.CLOSED);

        List<SimplePropertyRobustness> rob1 = pred1.evaluate(trajectory1, interval);
        List<SimplePropertyRobustness> rob2 = pred2.evaluate(trajectory2, interval);        
        List<SimplePropertyRobustness> rob = monitor.evaluate(rob1, rob2, interval);        

        /* Collect all time points in which values should be checked. */
        TreeSet<Float> controlTimes = new TreeSet();
        Iterator<SimplePropertyRobustness> it = rob.iterator();
        while (it.hasNext())
        {
            controlTimes.add(it.next().getTime());
        }
        it = rob1.iterator();
        while (it.hasNext())
        {
            controlTimes.add(it.next().getTime());
        }
        it = rob2.iterator();
        while (it.hasNext())
        {
            controlTimes.add(it.next().getTime());
        }

        Iterator<Float> tit = controlTimes.iterator();
        while (tit.hasNext())
        {
            float time = tit.next();
            SimplePropertyRobustness pr = super.getRobustnessValue(rob, time);
            SimplePropertyRobustness pr1 = super.getRobustnessValue(rob1, time);
            SimplePropertyRobustness pr2 = super.getRobustnessValue(rob2, time);

            assertTrue(pr.getTime() == pr1.getTime() && pr.getTime() == pr2.getTime(),"IT0: Points differ in time.");            
            assertEquals(pr.value(), Math.min(pr1.value(), pr2.value()), errorTolerance,
                    "IT0: The result differs for time " + pr.getTime() + 
                    " (pr1 = " + pr1.value() + ", pr2 = " + pr2.value() +
                    ", pr = " + pr.value() + ")");
            if (pr1.value() == pr2.value())
            {
                assertEquals(pr.getValueDerivative(), Math.min(pr1.getValueDerivative(), pr2.getValueDerivative()), errorTolerance,
                    "IT0: The result derivatives differ for time " + pr.getTime() + 
                    " (Dpr1 = " + pr1.getValueDerivative() + ", Dpr2 = " + pr2.getValueDerivative() +
                    ", Dpr = " + pr.getValueDerivative() + ")");
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
