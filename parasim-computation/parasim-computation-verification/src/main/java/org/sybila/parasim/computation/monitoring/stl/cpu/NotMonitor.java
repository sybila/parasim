package org.sybila.parasim.computation.monitoring.stl.cpu;

import java.util.List;
import java.util.ArrayList;
import org.sybila.parasim.computation.monitoring.PropertyRobustness;
import org.sybila.parasim.model.trajectory.Trajectory;
import java.util.Iterator;

/**
 * Monitors the negation of a subformula. The output is a negation of the signal.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public class NotMonitor<T extends Trajectory>
       implements Evaluable<T, SimplePropertyRobustness>
{
    private Evaluable<T, PropertyRobustness> sub;

    public NotMonitor(Evaluable<T, PropertyRobustness> subExpression)
    {
        if (subExpression == null)
        {
            throw new NullPointerException("Parameter sub is null.");
        }
        this.sub = subExpression;
    }


    public List<SimplePropertyRobustness> evaluate(T trajectory, float a, float b)
    {
        List<PropertyRobustness> subResult = sub.evaluate(trajectory, a, b);
        ArrayList<SimplePropertyRobustness> result = new ArrayList<SimplePropertyRobustness>();
        Iterator<PropertyRobustness> it = subResult.iterator();
        while (it.hasNext())
        {
            PropertyRobustness value = it.next();
            result.add(new SimplePropertyRobustness(value.getTime(), -value.value(), -value.getValueDerivative()));
        }
        return result;
    }

}
