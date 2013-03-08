package org.sybila.parasim.computation.verification.stlstar.cpu;

import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.computation.verification.api.Monitor;
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
public class NotStarMonitor extends AbstractStarMonitor {

    private final StarMonitor sub;

    /**
     * Create a monitor for a negation of subformula.
     *
     * @param property This formula.
     * @param info Star information about root formula.
     * @param subMonitor Monitor of subformula.
     */
    public NotStarMonitor(Formula property, FormulaStarInfo info, StarMonitor subMonitor) {
        super(property, info);
        Validate.notNull(subMonitor);
        sub = subMonitor;
    }

    @Override
    protected Robustness computeRobustness(Coordinate index) {
        return sub.getRobustness(index).invert();
    }

    @Override
    public Collection<Monitor> getSubmonitors() {
        return Arrays.asList((Monitor) sub);
    }

    @Override
    public int size() {
        return sub.size();
    }
}
