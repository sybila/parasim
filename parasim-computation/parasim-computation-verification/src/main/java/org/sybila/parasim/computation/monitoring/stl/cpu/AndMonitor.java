package org.sybila.parasim.computation.monitoring.stl.cpu;

import java.util.List;
import java.util.ArrayList;
import org.sybila.parasim.computation.monitoring.PropertyRobustness;
import org.sybila.parasim.model.trajectory.Trajectory;
import java.util.Iterator;

/**
 * Monitors the conjunction of two subformulas. The output is the minimum of
 * the two signals.
 * 
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public class AndMonitor<T extends Trajectory>
       implements Evaluable<T, SimplePropertyRobustness>
{
    private Evaluable<T, PropertyRobustness> sub1;
    private Evaluable<T, PropertyRobustness> sub2;

    public AndMonitor(Evaluable<T, PropertyRobustness> sub1, Evaluable<T, PropertyRobustness> sub2)
    {
        if (sub1 == null)
        {
            throw new NullPointerException("Parameter sub1 is null.");
        }
        if (sub2 == null)
        {
            throw new NullPointerException("Parameter sub2 is null.");
        }
        this.sub1 = sub1;
        this.sub2 = sub2;
    }

    public List<SimplePropertyRobustness> evaluate(T trajectory, float a, float b)
    {
        List<PropertyRobustness> list1 = sub1.evaluate(trajectory, a, b);
        List<PropertyRobustness> list2 = sub2.evaluate(trajectory, a, b);
        ArrayList<SimplePropertyRobustness> result = new ArrayList<SimplePropertyRobustness>();
        int lastPointOrigin;

        //FIXME


    }

}
