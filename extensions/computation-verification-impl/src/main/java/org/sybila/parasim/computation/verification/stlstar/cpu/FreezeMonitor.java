package org.sybila.parasim.computation.verification.stlstar.cpu;

import org.sybila.parasim.computation.verification.cpu.StarMonitor;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.model.verification.stl.FreezeFormula;
import org.sybila.parasim.model.verification.stlstar.FormulaStarInfo;
import org.sybila.parasim.util.Coordinate;

/**
 * Monitors a frozen subformula.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class FreezeMonitor extends NonTemporalUnaryStarMonitor {

    private final int freeze;

    /**
     * Create a monitor for a frozen subformula.
     * @param property This formula.
     * @param info Information about the root formula.
     * @param subMonitor Monitor of subformula.
     */
    public FreezeMonitor(FreezeFormula property, FormulaStarInfo info, StarMonitor subMonitor) {
        super(property, info, subMonitor);
        freeze = property.getFreezeIndex();
    }

    @Override
    protected Robustness computeRobustness(Coordinate index) {
        Coordinate.Builder coord = new Coordinate.Builder(index);
        coord.setCoordinate(freeze, index.getCoordinate(0));
        return getSubMonitor().getRobustness(coord.create());
    }
}
