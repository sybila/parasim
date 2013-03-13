package org.sybila.parasim.model.verification.stlstar;

import org.apache.commons.lang3.Validate;
import org.sybila.parasim.model.verification.stl.AndFormula;
import org.sybila.parasim.model.verification.stl.Formula;
import org.sybila.parasim.model.verification.stl.FreezeFormula;
import org.sybila.parasim.model.verification.stl.FutureFormula;
import org.sybila.parasim.model.verification.stl.GloballyFormula;
import org.sybila.parasim.model.verification.stl.NotFormula;
import org.sybila.parasim.model.verification.stl.OrFormula;
import org.sybila.parasim.model.verification.stl.UntilFormula;

/**
 * Removes all freeze operators which do not correspond to an open frozen time
 * value.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ClosedFreezeRemover {

    private final FormulaStarInfo info;
    private final Formula result;

    private ClosedFreezeRemover(Formula targetFormula) {
        info = new FormulaStarInfo(targetFormula);
        result = removeFreezes(targetFormula);
    }

    private Formula removeFreezes(Formula target) {
        switch (target.getType()) {
            case FREEZE:
                int index = ((FreezeFormula) target).getFreezeIndex();
                if (info.getUnfrozenIndices(target.getSubformula(0)).contains(index)) {
                    return new FreezeFormula(removeFreezes(target.getSubformula(0)), index);
                } else {
                    return removeFreezes(target.getSubformula(0));
                }
            case NOT:
                return new NotFormula(removeFreezes(target.getSubformula(0)));
            case AND:
                return new AndFormula(removeFreezes(target.getSubformula(0)), removeFreezes(target.getSubformula(1)));
            case OR:
                return new OrFormula(removeFreezes(target.getSubformula(0)), removeFreezes(target.getSubformula(1)));
            case FUTURE:
                return new FutureFormula(removeFreezes(target.getSubformula(0)), ((FutureFormula) target).getInterval());
            case GLOBALLY:
                return new GloballyFormula(removeFreezes(target.getSubformula(0)), ((GloballyFormula) target).getInterval());
            case UNTIL:
                return new UntilFormula(removeFreezes(target.getSubformula(0)), removeFreezes(target.getSubformula(1)), ((UntilFormula) target).getInterval());
            case PREDICATE:
                return target;
            default:
                throw new IllegalArgumentException("Unknown formula type.");
        }
    }

    /**
     * Remove all superfluous freeze operators from a formula.
     *
     * @param target Target formula.
     * @return Target formula without superfluous freeze operators.
     */
    public static Formula removeClosedFreezes(Formula target) {
        Validate.notNull(target);
        ClosedFreezeRemover computation = new ClosedFreezeRemover(target);
        return computation.result;
    }
}
