package org.sybila.parasim.computation.verification.stlstar.cpu;

import org.sybila.parasim.computation.verification.cpu.StarMonitor;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.model.verification.stl.Formula;
import org.sybila.parasim.model.verification.stlstar.FormulaStarInfo;
import org.sybila.parasim.util.Coordinate;

/**
 * Monitors a negation of subformula.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class NotStarMonitor extends NonTemporalUnaryStarMonitor {

    /**
     * Create a monitor for a negation of subformula.
     *
     * @param property This formula.
     * @param info Star information about root formula.
     * @param subMonitor Monitor of subformula.
     */
    public NotStarMonitor(Formula property, FormulaStarInfo info, StarMonitor subMonitor) {
        super(property, info, subMonitor);
    }

    @Override
    protected Robustness computeRobustness(Coordinate index) {
        return getSubMonitor().getRobustness(index).invert();
    }
}
