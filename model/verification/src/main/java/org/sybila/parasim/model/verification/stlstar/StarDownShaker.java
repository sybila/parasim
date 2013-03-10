package org.sybila.parasim.model.verification.stlstar;

import java.util.HashSet;
import java.util.Set;
import org.sybila.parasim.model.verification.stl.AndFormula;
import org.sybila.parasim.model.verification.stl.Formula;
import org.sybila.parasim.model.verification.stl.FormulaInterval;
import org.sybila.parasim.model.verification.stl.FreezeFormula;
import org.sybila.parasim.model.verification.stl.FutureFormula;
import org.sybila.parasim.model.verification.stl.GloballyFormula;
import org.sybila.parasim.model.verification.stl.NotFormula;
import org.sybila.parasim.model.verification.stl.OrFormula;
import org.sybila.parasim.model.verification.stl.Predicate;
import org.sybila.parasim.model.verification.stl.TemporalFormula;
import org.sybila.parasim.model.verification.stl.UntilFormula;

/**
 * Moves freeze operators down the formula syntactic tree until they reach
 * either temporal operator or a predicate (leaf). Freeze operator before
 * predicates are merged with predicates.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public enum StarDownShaker {

    /**
     * Singleton instance.
     */
    INSTANCE;

    private Formula reconstructFreezes(Formula subformula, Set<Integer> freezes) {
        Formula result = subformula;
        for (Integer i : freezes) {
            result = new FreezeFormula(result, i);
        }
        return result;
    }

    private Set<Integer> createEmptySet() {
        return new HashSet<>();
    }

    private FormulaInterval getInterval(Formula target) {
        if (!(target instanceof TemporalFormula)) {
            throw new IllegalArgumentException("Given formula is not temporal.");
        }
        return ((TemporalFormula) target).getInterval();
    }

    /**
     * Move operators down in given formula.
     *
     * @param input Formula to be transformed.
     * @return Transformed formula.
     */
    public Formula downShake(Formula input) {
        return downShake(input, createEmptySet());
    }

    private Formula downShake(Formula input, Set<Integer> freezes) {
        switch (input.getType()) {
            case FREEZE:
                freezes.add(((FreezeFormula) input).getFreezeIndex());
                return downShake(input.getSubformula(0), freezes);
            case UNTIL:
                return reconstructFreezes(new UntilFormula(
                        downShake(input.getSubformula(0)),
                        downShake(input.getSubformula(1)),
                        getInterval(input)),
                        freezes);
            case FUTURE:
                return reconstructFreezes(new FutureFormula(
                        downShake(input.getSubformula(0)),
                        getInterval(input)),
                        freezes);
            case GLOBALLY:
                return reconstructFreezes(new GloballyFormula(
                        downShake(input.getSubformula(0)),
                        getInterval(input)),
                        freezes);
            case AND:
                return new AndFormula(
                        downShake(input.getSubformula(0), new HashSet<>(freezes)),
                        downShake(input.getSubformula(1), freezes));
            case OR:
                return new OrFormula(
                        downShake(input.getSubformula(0), new HashSet<>(freezes)),
                        downShake(input.getSubformula(1), freezes));
            case NOT:
                return new NotFormula(downShake(input.getSubformula(0), freezes));
            case PREDICATE:
                return ((Predicate) input).mergeFrozenDimensions(freezes);
            default:
                throw new IllegalArgumentException("Unknown formula type.");
        }
    }
}
