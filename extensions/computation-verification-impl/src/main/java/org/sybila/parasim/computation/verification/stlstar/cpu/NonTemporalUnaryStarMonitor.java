package org.sybila.parasim.computation.verification.stlstar.cpu;

import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.computation.verification.api.Monitor;
import org.sybila.parasim.computation.verification.cpu.StarMonitor;
import org.sybila.parasim.model.verification.stl.Formula;
import org.sybila.parasim.model.verification.stlstar.FormulaStarInfo;

/**
 * Monitor of an unary operator which is not temporal (i.e. not and freeze).
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public abstract class NonTemporalUnaryStarMonitor extends AbstractStarMonitor {

    private final StarMonitor sub;

    /**
     * Create a monitor for a unary non-temporal operator.
     *
     * @param property This formula.
     * @param info Star information about root formula.
     * @param subMonitor Monitor of subformula.
     */
    public NonTemporalUnaryStarMonitor(Formula property, FormulaStarInfo info, StarMonitor subMonitor) {
        super(property, info);
        Validate.notNull(subMonitor);
        sub = subMonitor;
    }

    /**
     * Returns monitor of subformula.
     *
     * @return Monitor of subformula.
     */
    protected StarMonitor getSubMonitor() {
        return sub;
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
