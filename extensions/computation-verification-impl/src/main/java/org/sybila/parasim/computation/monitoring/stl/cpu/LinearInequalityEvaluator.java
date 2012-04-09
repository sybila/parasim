package org.sybila.parasim.computation.monitoring.stl.cpu;

import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.PointDerivative;

/**
 * Enables the evaluation of linear functions over trajectories.
 * The functions are expected to be of the form:
 *    k1 * x[n1] + k2 * x[n2] + ... + km * x[nm] > const
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public class LinearInequalityEvaluator implements PredicateEvaluator<SimplePropertyRobustness> {

    private int[] dimIndexes;
    private float[] coeficients;
    private float constant;
    private InequalityType op;

    /**
     * Indexes n1...nm are in the dimIndexes array.
     * Coeficients k1..km are in the coeficients array.
     * Both arrays must have same lenght.
     *
     * @param dimIndexes Indexes of dimensions in linear function.
     * @param coeficients Coeficients to weight dimensional values.
     * @param constant Number to compare to.
     * @param op Typy of inequality.
     */
    LinearInequalityEvaluator(int[] dimIndexes, float[] coeficients, float constant, InequalityType op) {
        if (dimIndexes == null) {
            throw new IllegalArgumentException("Parameter dimIndexes is null.");
        }
        if (coeficients == null) {
            throw new IllegalArgumentException("Parameter coeficients is null.");
        }
        if (dimIndexes.length != coeficients.length) {
            throw new IllegalArgumentException("The dimIndexes and coeficients arrays have different length.");
        }
        if (dimIndexes.length == 0) {
            throw new IllegalArgumentException("The dimIndexes array is empty.");
        }
        for (int i = 0; i < dimIndexes.length; i++) {
            if (dimIndexes[i] < 0) {
                throw new IllegalArgumentException("Value in dimIndexes[" + i + "] is negative (" + dimIndexes[i] + ").");
            }
        }
        this.dimIndexes = dimIndexes;
        this.coeficients = coeficients;
        this.constant = constant;
        this.op = op;
    }

    @Override
    public SimplePropertyRobustness value(Point p, Point next) {
        float exp1 = 0.0f;
        float exp2 = 0.0f;
        for (int i = 0; i < dimIndexes.length; i++) {
            exp1 += p.getValue(dimIndexes[i]) * coeficients[i];
            exp2 += next.getValue(dimIndexes[i]) * coeficients[i];
        }
        return new SimplePropertyRobustness(p.getTime(),
                op.value(exp1, constant),
                op.derivative((exp2 - exp1) / (next.getTime() - p.getTime())));
    }

    @Override
    public String toString() {
        String result = "(";
        for (int i = 0; i < dimIndexes.length; i++) {
            result += coeficients[i] + " * X[" + dimIndexes[i] + "]";
            if (i < dimIndexes.length - 1) {
                result += " + ";
            }
        }
        return result + " " + op.toString() + " " + constant + ")";
    }

    @Override
    public SimplePropertyRobustness value(PointDerivative p) {
        float dValue = 0.0f;
        float value = 0.0f;
        for (int i = 0; i < dimIndexes.length; i++) {
            value += p.getValue(dimIndexes[i]) * coeficients[i];
            dValue += p.getDerivative(dimIndexes[i]) * coeficients[i];
        }

        return new SimplePropertyRobustness(p.getTime(),
                op.value(value, constant),
                op.derivative(dValue));
    }
}
