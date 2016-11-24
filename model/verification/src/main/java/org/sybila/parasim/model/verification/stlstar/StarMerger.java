/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.model.verification.stlstar;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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

    private StarMerger(Formula targetFormula) {
        target = targetFormula;

        // add freeze operators to the start //
        info = new FormulaStarInfo(target);
        for (Integer index : info.getUnfrozenIndices(target)) {
            target = new FreezeFormula(target, index);
        }

        // shake down freezes //
        target = StarDownShaker.INSTANCE.downShake(target);

        // remove superfluous freezes //
        target = ClosedFreezeRemover.removeClosedFreezes(target);

        // remove duplicate freezes //
        num = 0;
        removeDuplicates(target);
        target = substitute(target, new HashMap<Integer, Integer>());

        // compute substitution //
        info = new FormulaStarInfo(target);
        num = info.getMaxUnfrozenIndices();
        computeSubstitution(target, new HashMap<Integer, Integer>());
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

    private void computeSubstitution(Formula input, Map<Integer, Integer> indexSubstitution) {
        // remove unfrozen indices from substitition //
        Collection<Integer> freeVars = info.getUnfrozenIndices(input);
        Iterator<Map.Entry<Integer, Integer>> iter = indexSubstitution.entrySet().iterator();
        while (iter.hasNext()) {
            if (!freeVars.contains(iter.next().getKey())) {
                iter.remove();
            }
        }

        // is it freeze? //
        if (input.getType() == FormulaType.FREEZE) {
            int index = ((FreezeFormula) input).getFreezeIndex();
            // get first free index //
            int var = 1;
            while (var <= num && indexSubstitution.containsValue(var)) {
                var++;
            }
            if (var > num) {
                throw new RuntimeException("There is no other free index. Computation of maximum star number incorrect.");
            }

            // make a substitution //
            indexSubstitution.put(index, var);
            substitution.put(input, var);
            computeSubstitution(input.getSubformula(0), indexSubstitution);
        } else {
            // propagate //
            for (int i = 0; i < input.getArity(); i++) {
                computeSubstitution(input.getSubformula(i), new HashMap<>(indexSubstitution));
            }
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
