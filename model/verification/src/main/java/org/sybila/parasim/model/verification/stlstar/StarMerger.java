package org.sybila.parasim.model.verification.stlstar;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.sybila.parasim.model.verification.stl.AndFormula;
import org.sybila.parasim.model.verification.stl.Formula;
import org.sybila.parasim.model.verification.stl.FormulaType;
import org.sybila.parasim.model.verification.stl.FreezeFormula;
import org.sybila.parasim.model.verification.stl.FutureFormula;
import org.sybila.parasim.model.verification.stl.GloballyFormula;
import org.sybila.parasim.model.verification.stl.NotFormula;
import org.sybila.parasim.model.verification.stl.OrFormula;
import org.sybila.parasim.model.verification.stl.Predicate;
import org.sybila.parasim.model.verification.stl.UntilFormula;

/**
 * Minimizes formula. Constitutes of the following steps: <ol> <li>Remove freeze
 * operators which have no corresponding variable in predicate (see {@link ClosedFreezeRemover}).</li>
 * <li>Add "missing" freezes to the beginning of the formula (i.e. those whose
 * indices are open).</li> <li>Shake down freezes to temporal operators or
 * predicates (see {@link StarDownShaker}).</li> <li>Remove duplicate freeze
 * operators.</li> <li>Compute minimum freeze operator indices.</li> This
 * operation should simplify monitoring of "inefficient" formulae.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class StarMerger {

    private FormulaStarInfo info;
    private Formula target;
    private Map<Formula, Integer> substitution = new HashMap<>();
    private int num;

    private Set<Integer> createFullSet(int max) {
        Set<Integer> result = new HashSet<>();
        for (int i = 1; i <= max; i++) {
            result.add(i);
        }
        return result;
    }

    private StarMerger(Formula targetFormula) {
        target = targetFormula;

        // remove superfluous freezes //
        target = ClosedFreezeRemover.removeClosedFreezes(target);

        // add freeze operators to the start //
        info = new FormulaStarInfo(target);
        for (Integer index : info.getUnfrozenIndices(target)) {
            target = new FreezeFormula(target, index);
        }

        // first shake down freezes //
        target = StarDownShaker.INSTANCE.downShake(target);

        // remove duplicate freezes //
        num = 0;
        removeDuplicates(target);
        target = substitute(target, new HashMap<Integer, Integer>());

        // compute substitution //
        info = new FormulaStarInfo(target);
        num = info.getMaxUnfrozenIndices();
        computeSubstitution(target, createFullSet(num), new HashMap<Integer, Integer>());
        target = substitute(target, new HashMap<Integer, Integer>());
    }

    private Formula substitute(Formula input, Map<Integer, Integer> indexSubstitution) {
        switch (input.getType()) {
            case FREEZE:
                Integer index = substitution.get(input);
                if (index == null) {
                    return new FreezeFormula(substitute(input.getSubformula(0), indexSubstitution),
                            ((FreezeFormula) input).getFreezeIndex());
                } else {
                    while (input.getType() == FormulaType.FREEZE) {
                        indexSubstitution.put(((FreezeFormula) input).getFreezeIndex(), index);
                        input = input.getSubformula(0);
                    }
                    return new FreezeFormula(substitute(input, indexSubstitution), index);
                }
            case PREDICATE:
                return ((Predicate) input).substituteStars(indexSubstitution);
            case NOT:
                return new NotFormula(substitute(input.getSubformula(0), indexSubstitution));
            case FUTURE:
                return new FutureFormula(substitute(input.getSubformula(0), indexSubstitution), ((FutureFormula) input).getInterval());
            case GLOBALLY:
                return new GloballyFormula(substitute(input.getSubformula(0), indexSubstitution), ((GloballyFormula) input).getInterval());
            case AND:
                return new AndFormula(substitute(input.getSubformula(0), new HashMap<>(indexSubstitution)), substitute(input.getSubformula(1), indexSubstitution));
            case OR:
                return new OrFormula(substitute(input.getSubformula(0), new HashMap<>(indexSubstitution)), substitute(input.getSubformula(1), indexSubstitution));
            case UNTIL:
                return new UntilFormula(substitute(input.getSubformula(0), new HashMap<>(indexSubstitution)),
                        substitute(input.getSubformula(1), indexSubstitution), ((UntilFormula) input).getInterval());
            default:
                throw new IllegalArgumentException("Unknown formula type.");
        }
    }

    private void removeDuplicates(Formula input) {
        // give each sequence of freezes own number //
        if (input.getType() == FormulaType.FREEZE) {
            num++;
            substitution.put(input, num);
            // jump over all duplicates //
            do {
                input = input.getSubformula(0);
            } while (input.getType() == FormulaType.FREEZE);
            removeDuplicates(input);
        } else {
            for (int i = 0; i < input.getArity(); i++) {
                removeDuplicates(input.getSubformula(i));
            }
        }
    }

    private void computeSubstitution(Formula input, Set<Integer> freeIndices, Map<Integer, Integer> indexSubstitution) {
        // free frozen-time indices which are not substituted on this branch//
        for (Integer index : info.getUnfrozenIndices(input)) {
            Integer substituted = indexSubstitution.get(index);
            if (substituted != null) {
                freeIndices.add(substituted);
            }
        }

        // is it freeze? //
        if (input.getType() == FormulaType.FREEZE) {
            int index = ((FreezeFormula) input).getFreezeIndex();
            // make a substitution //
            Integer var = freeIndices.iterator().next();
            freeIndices.remove(var);
            indexSubstitution.put(index, var);
            computeSubstitution(input.getSubformula(0), freeIndices, indexSubstitution);
        } else if (input.getArity() > 0) {
            // propagate //
            for (int i = 1; i < input.getArity(); i++) {
                computeSubstitution(input.getSubformula(i), new HashSet<>(freeIndices), new HashMap<>(indexSubstitution));
            }
            computeSubstitution(input.getSubformula(0), freeIndices, indexSubstitution);
        }
    }

    /**
     * Transform target formula.
     *
     * @param target Formula to by transformed.
     * @return Result of transformation.
     */
    public static Formula mergeStars(Formula target) {
        StarMerger computation = new StarMerger(target);
        return computation.target;
    }
}
