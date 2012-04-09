package org.sybila.parasim.computation.monitoring.stl.cpu;

import org.sybila.parasim.model.verification.stl.TimeInterval;
import java.util.List;
import java.util.ArrayList;
import org.sybila.parasim.computation.monitoring.api.PropertyRobustness;
import org.sybila.parasim.model.trajectory.Trajectory;
import java.util.Iterator;

/**
 * Monitors the negation of a subformula. The output is a negation of the signal.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public class NotMonitor<T extends Trajectory>
        implements Evaluable<T, SimplePropertyRobustness> {

    private Evaluable<T, PropertyRobustness> sub;

    public NotMonitor(Evaluable<T, PropertyRobustness> subExpression) {
        if (subExpression == null) {
            throw new NullPointerException("Parameter sub is null.");
        }
        this.sub = subExpression;
    }

    @Override
    public List<SimplePropertyRobustness> evaluate(T trajectory, TimeInterval interval) {
        List<PropertyRobustness> subResult = sub.evaluate(trajectory, interval);
        ArrayList<SimplePropertyRobustness> result = new ArrayList<SimplePropertyRobustness>();
        Iterator<PropertyRobustness> it = subResult.iterator();
        while (it.hasNext()) {
            PropertyRobustness value = it.next();
            result.add(new SimplePropertyRobustness(value.getTime(), -value.value(), -value.getValueDerivative()));
        }
        return result;
    }
}
