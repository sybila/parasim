package org.sybila.parasim.computation.monitoring.stl.cpu;

import java.util.List;
import java.util.ArrayList;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.PointDerivative;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.trajectory.CyclicTrajectory;
import java.util.Iterator;

/**
 * PredicateMonitors transform primary signals (point values) to secondary
 * signals (function values) by the use of PredicateEvaluators over the course
 * of a trajectory.
 * 
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dra�an</a>
 */
public class PredicateMonitor<T extends Trajectory>
       implements Evaluable<T, SimplePropertyRobustness>
{
    private PredicateEvaluator<SimplePropertyRobustness> evaluator;
    private ArrayList<SimplePropertyRobustness> cache;

    public PredicateMonitor(PredicateEvaluator<SimplePropertyRobustness> e)
    {
        if (e == null)
        {
            throw new IllegalArgumentException("Parameter e is null.");
        }
        this.evaluator = e;
    }

    /**
     * Evaluates a predicate over the course of the trajectory on the interval
     * [a,b]. If the trajectory is not long enough the last point is extrapolated
     * into the future. If the trajectory is cyclic evaluation loops through
     * the cycle so many times as to cover the whole interval.
     *
     * The result list will have as many elements as there where points on the
     * given trajectory in the interval [a,b] with 1 possible extra point in case
     * that there was no point with the exact time value equal to a.
     *
     * @param trajectory Trajectory or CyclicTrajectory over which to evaluate predicate.
     * @param a Start of time interval.
     * @param b End of time interval.
     * @return List of predicate values in points of trajectory covering the given interval.
     */
    @Override
    public List<SimplePropertyRobustness> evaluate(T trajectory, float a, float b)
    {
        if (a < 0)
        {
            throw new IllegalArgumentException("Parameter a must be >= 0.");
        }
        if (b < a)
        {
            throw new IllegalArgumentException("Parameter b must be greater or equal to parameter a.");
        }
        if (trajectory == null)
        {
            throw new IllegalArgumentException("Parameter trajectory is null.");
        }
        if (trajectory.getLength() == 0)
        {
            throw new IllegalArgumentException("The trajectory is empty.");
        }
        Iterator<Point> it;
        if (trajectory.getClass().isInstance(CyclicTrajectory.class))
        {
            it = ((CyclicTrajectory)trajectory).cyclicIterator();
        }
        else
        {                
            it = trajectory.iterator();
        }
        Point p1 = it.next(); /* A trajectory must always contain at least 1 point */
        Point p2 = p1;
        /* The trajectory is expected to hold points of only one class type */
        if (p1.getClass().isInstance(PointDerivative.class))
        {
            while (it.hasNext() && p1.getTime() < a)
            {
                p2 = p1;
                p1 = it.next();
            }
            if (p1.getTime() >= a)
            {
                ArrayList<SimplePropertyRobustness> list = new ArrayList<SimplePropertyRobustness>();

                if (p1.getTime() == a)
                {
                    SimplePropertyRobustness tmp = evaluator.value((PointDerivative)p1);
                    list.add(tmp);
                }
                else /* p1.getTime() > a */
                {
                    SimplePropertyRobustness tmp = evaluator.value((PointDerivative)p2);
                    tmp = new SimplePropertyRobustness(a, tmp.value() + tmp.getValueDerivative() * (a - tmp.getTime()),
                                                 tmp.getValueDerivative());
                    list.add(tmp);
                }
                while (it.hasNext() && p1.getTime() < b)
                {
                    list.add(evaluator.value((PointDerivative)p1));
                    p1 = it.next();
                }
                list.trimToSize();
                return list;
            }
            else if (p1.getTime() < a)
            {
                /* Trajectory ended before the begining of the interval [a,b] was reached
                   therefore the last point is used to evaluate the predicate and because
                   the evaluation function is expected to be linear we can use it's value
                   to extrapolate any further value after the end of the trajectory. */
                SimplePropertyRobustness tmp = evaluator.value((PointDerivative)p1);
                SimplePropertyRobustness result =
                    new SimplePropertyRobustness(a, tmp.value() + tmp.getValueDerivative() * (a - tmp.getTime()),
                                                 tmp.getValueDerivative());
                ArrayList<SimplePropertyRobustness> list = new ArrayList<SimplePropertyRobustness>(1);
                list.add(result);
                return list;
            }
        }
        else
        {
            while (it.hasNext() && p1.getTime() <= a)
            {
                p2 = p1;
                p1 = it.next();
            }
                        
            ArrayList<SimplePropertyRobustness> list = new ArrayList<SimplePropertyRobustness>();

            /* The begining of the interval [a,b] is approximated from p2 and p1 even
             * if the trajectory ended before the begining was reached.
             */

            SimplePropertyRobustness tmp = evaluator.value(p2,p1);
            tmp = new SimplePropertyRobustness(a, tmp.value() + tmp.getValueDerivative() * (a - tmp.getTime()),
                                         tmp.getValueDerivative());
            list.add(tmp);

            while (it.hasNext() && p1.getTime() < b)
            {
                p2 = p1;
                p1 = it.next();
                list.add(evaluator.value(p2,p1));
            }
            list.trimToSize();
            return list;            
        }
        return null;
    }

}
