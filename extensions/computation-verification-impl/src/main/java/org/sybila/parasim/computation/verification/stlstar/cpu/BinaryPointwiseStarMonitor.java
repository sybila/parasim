package org.sybila.parasim.computation.verification.stlstar.cpu;

import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.computation.verification.api.Monitor;
import org.sybila.parasim.computation.verification.cpu.StarMonitor;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.model.verification.SimpleRobustness;
import org.sybila.parasim.model.verification.stl.Formula;
import org.sybila.parasim.model.verification.stlstar.FormulaStarInfo;
import org.sybila.parasim.util.Coordinate;

/**
 * Monitors pointwise binary operators.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public abstract class BinaryPointwiseStarMonitor extends AbstractStarMonitor {

    private final StarMonitor left, right;

    /**
     * Create pointwise binary monitor from subformulae monitors.
     *
     * @param property Monitored formula.
     * @param info Star information of root formula.
     * @param left Monitor of left formula.
     * @param right Monitor of right formula.
     */
    public BinaryPointwiseStarMonitor(Formula property, FormulaStarInfo info, StarMonitor left, StarMonitor right) {
        super(property, info);
        Validate.notNull(left);
        Validate.notNull(right);
        this.left = left;
        this.right = right;
    }

    @Override
    protected Robustness computeRobustness(Coordinate index) {
        Robustness leftRobustness = left.getRobustness(index);
        return new SimpleRobustness(evaluate(leftRobustness, right.getRobustness(index)), leftRobustness.getTime(), getProperty());
    }

    /**
     * Evaluation function from left and right value.
     *
     * @param left Left value.
     * @param right Right value.
     * @return Robustness computed from left and right values.
     */
    protected abstract float evaluate(Robustness left, Robustness right);

    @Override
    public Collection<Monitor> getSubmonitors() {
        return Arrays.asList((Monitor) left, (Monitor) right);
    }

    @Override
    public int size() {
        return Math.min(left.size(), right.size());
    }
}
