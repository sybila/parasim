package org.sybila.parasim.model.verification.stlstar;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.sybila.parasim.model.verification.stl.Formula;
import org.sybila.parasim.model.verification.stl.FreezeFormula;
import org.sybila.parasim.model.verification.stl.Predicate;
import org.sybila.parasim.model.verification.stl.TemporalFormula;

/**
 * Additional functions for formula semantic equivalence.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public final class FormulaEquality {

    private FormulaEquality() {
    }

    /**
     * Formulae have the same structure and are equal semantically but actual
     * frozen-time indices may differ.
     *
     * @return
     * <code>true</code> when formulae are equivalent,
     * <code>false</code> otherwise.
     */
    public static boolean equalUpToFreezeIndices(Formula phi, Formula psi) {
        return equalUpToFreezeIndices(phi, psi, new HashMap<Integer, Integer>(), new FormulaStarInfo(phi));
    }

    private static boolean equalUpToFreezeIndices(Formula phi, Formula psi, Map<Integer, Integer> phiToPsi, final FormulaStarInfo phiInfo) {
        if (phi.getArity() != psi.getArity()) {
            return false;
        }
        if (phi.getType() != psi.getType()) {
            return false;
        }

        // remove substitution for those indices which are not free //
        Collection<Integer> freeVars = phiInfo.getUnfrozenIndices(phi);
        Iterator<Map.Entry<Integer, Integer>> iter = phiToPsi.entrySet().iterator();
        while (iter.hasNext()) {
            if (!freeVars.contains(iter.next().getKey())) {
                iter.remove();
            }
        }

        switch (phi.getType()) {
            case PREDICATE:
                // this is the important place -- the important things are whether predicate are equal //
                return ((Predicate) phi).substituteStars(phiToPsi).equals(psi);
            // TEMPORAL FORMULAE //
            case UNTIL:
            case GLOBALLY:
            case FUTURE:
                if (!((TemporalFormula) phi).getInterval().equals(((TemporalFormula) psi).getInterval())) {
                    return false;
                }
                break;
            case FREEZE:
                int phiIndex = ((FreezeFormula) phi).getFreezeIndex();
                int psiIndex = ((FreezeFormula) psi).getFreezeIndex();
                phiToPsi.put(phiIndex, psiIndex);
                break;
        }

        for (int i = 0; i < phi.getArity(); i++) {
            if (!equalUpToFreezeIndices(phi.getSubformula(i), psi.getSubformula(i), new HashMap<>(phiToPsi), phiInfo)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Formulae have the same semantics and same structure barring freeze
     * operators, which may be moved or merged with predicates (while not
     * changing formula semantics).
     *
     * @return
     * <code>true</code> when formulae are equal,
     * <code>false</code> otherwise.
     */
    public static boolean equalUpToFreezes(Formula phi, Formula psi) {
        return equalUpToFreezeIndices(StarMerger.mergeStars(phi), StarMerger.mergeStars(psi));
    }
}
