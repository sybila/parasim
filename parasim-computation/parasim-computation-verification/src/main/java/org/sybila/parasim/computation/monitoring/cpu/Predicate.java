package org.sybila.parasim.computation.monitoring.cpu;

import java.util.List;
import java.util.ArrayList;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.trajectory.TrajectoryIterator;

/**
 * Predicate to transform primary signals to secondary.
 * 
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public class Predicate<T extends Trajectory>
       implements Evaluable<T, SimplePropertyRobustness>
{
    private PredicateEvaluator<SimplePropertyRobustness> evaluator;

    public Predicate(PredicateEvaluator<SimplePropertyRobustness> e)
    {
        if (evaluator == null)
        {
            throw new IllegalArgumentException("Parameter e is null.");
        }
        this.evaluator = e;
    }

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
        TrajectoryIterator it = trajectory.iterator();
        Point p1 = it.next();
        Point p2 = p1;
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
                SimplePropertyRobustness tmp = evaluator.value(p1);
                list.add(tmp);
            }
            else /* p1.getTime() > a */
            {
                SimplePropertyRobustness tmp = evaluator.value(p2);
                tmp = new SimplePropertyRobustness(a, tmp.value() + tmp.getValueDerivative() * (a - tmp.getTime()),
                                             tmp.getValueDerivative());
                list.add(tmp);
            }
            while (it.hasNext() && p1.getTime() < b)
            {
                list.add(evaluator.value(p1));
                p1 = it.next();
            }
            list.trimToSize();
            return list;
        }
        else if (p1.getTime() < a)
        {
            SimplePropertyRobustness tmp = evaluator.value(p1);
            SimplePropertyRobustness result =
                new SimplePropertyRobustness(a, tmp.value() + tmp.getValueDerivative() * (a - tmp.getTime()),
                                             tmp.getValueDerivative());
            List<SimplePropertyRobustness> list = new ArrayList<SimplePropertyRobustness>(1);
            list.add(result);
            return list;
        }
        return null;
    }

}
