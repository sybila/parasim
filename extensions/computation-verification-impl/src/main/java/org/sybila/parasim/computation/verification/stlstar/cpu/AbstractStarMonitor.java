package org.sybila.parasim.computation.verification.stlstar.cpu;

import java.util.Collection;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.computation.verification.cpu.AbstractMonitor;
import org.sybila.parasim.computation.verification.cpu.StarMonitor;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.model.verification.stl.Formula;
import org.sybila.parasim.model.verification.stlstar.FormulaStarInfo;
import org.sybila.parasim.util.Coordinate;

/**
 * Monitor covering basis of STL monitoring with frozen-time values semantic.
 * Deals with differences between Monitor and Star monitor, also removes
 * unfrozen dimensions.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public abstract class AbstractStarMonitor extends AbstractMonitor implements StarMonitor {

    private final Collection<Integer> unfrozen;
    private final int starNum;

    public AbstractStarMonitor(Formula property, FormulaStarInfo info) {
        super(property);
        Validate.notNull(info);
        starNum = info.getStarNumber();
        unfrozen = info.getUnfrozenIndices(property);
    }

    protected Coordinate simplify(Coordinate index) {
        if (index.getDimension() <= starNum) {
            throw new IllegalArgumentException("Coordinate is too short to be evaluated.");
        }

        // create a coordinate of correct length //
        Coordinate.Builder coord = new Coordinate.Builder(index, starNum + 1);
        // set all frozen coordinates to zero //
        for (int i = 1; i <= starNum; i++) {
            if (!unfrozen.contains(new Integer(i))) {
                coord.setCoordinate(i, 0);
            }
        }
        return coord.create();
    }

    @Override
    public Robustness getRobustness(Coordinate index) {
        return computeRobustness(simplify(index));
    }

    /**
     * Procedure which actually computes the robustness. May expect that frozen
     * indices are zero.
     *
     * @param index Indices of time and frozen times.
     * @return Robustness in given time and frozen time.
     */
    protected abstract Robustness computeRobustness(Coordinate index);

    @Override
    public Robustness getRobustness(int index) {
        Coordinate.Builder coord = new Coordinate.Builder(starNum + 1);
        coord.setCoordinate(0, index);
        return getRobustness(coord.create());
    }
}
