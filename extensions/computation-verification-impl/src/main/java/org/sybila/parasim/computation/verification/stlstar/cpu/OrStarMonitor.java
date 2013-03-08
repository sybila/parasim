package org.sybila.parasim.computation.verification.stlstar.cpu;

import org.sybila.parasim.computation.verification.cpu.StarMonitor;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.model.verification.stl.Formula;
import org.sybila.parasim.model.verification.stlstar.FormulaStarInfo;

/**
 * Monitors logical disjunction of two subformulae.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class OrStarMonitor extends BinaryPointwiseStarMonitor {

    /**
     * Create disjunction monitor.
     *
     * @param property Monitored formula.
     * @param info Star information of root formula.
     * @param left Monitor of left formula.
     * @param right Monitor of right formula.
     */
    public OrStarMonitor(Formula property, FormulaStarInfo info, StarMonitor left, StarMonitor right) {
        super(property, info, left, right);
    }

    @Override
    protected float evaluate(Robustness left, Robustness right) {
        return Math.max(left.getValue(), right.getValue());
    }
}
