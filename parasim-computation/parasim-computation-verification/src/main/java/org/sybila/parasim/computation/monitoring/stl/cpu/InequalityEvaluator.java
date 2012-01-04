package org.sybila.parasim.computation.monitoring.stl.cpu;

import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.PointDerivative;

/**
 * Evaluates simple inequality predicates in given points.
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public class InequalityEvaluator implements PredicateEvaluator<SimplePropertyRobustness>
{
    private int dimIndex;
    private float constant;
    private InequalityType op;

    InequalityEvaluator(int dimIndex, float constant, InequalityType op)
    {
        if (dimIndex < 0)
        {
            throw new IllegalArgumentException("Parameter dimIndex must be >= 0.");
        }
        this.constant = constant;
        this.dimIndex = dimIndex;
        this.op = op;
    }

    @Override
    public SimplePropertyRobustness value(Point p, Point next)
    {
        return new SimplePropertyRobustness(p.getTime(), 
                op.value(p.getValue(dimIndex), constant),
                op.derivative(next.getValue(dimIndex) - p.getValue(dimIndex)));
    }

    @Override
    public String toString()
    {
        return "(X[" + dimIndex + "] " + op.toString() + " " + constant + ")";
    }

    @Override
    public SimplePropertyRobustness value(PointDerivative p)
    {
        return new SimplePropertyRobustness(p.getTime(),
                op.value(p.getValue(dimIndex), constant),
                op.derivative(p.getDerivative(dimIndex)));
    }
}
