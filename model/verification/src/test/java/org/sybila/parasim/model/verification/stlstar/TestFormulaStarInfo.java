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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.sybila.parasim.model.ode.PointVariableIdentity;
import org.sybila.parasim.model.verification.stl.AndFormula;
import org.sybila.parasim.model.verification.stl.Formula;
import org.sybila.parasim.model.verification.stl.FreezeFormula;
import org.sybila.parasim.model.verification.stl.FutureFormula;
import org.sybila.parasim.model.verification.stl.GloballyFormula;
import org.sybila.parasim.model.verification.stl.IntervalBoundaryType;
import org.sybila.parasim.model.verification.stl.LinearPredicate;
import org.sybila.parasim.model.verification.stl.NotFormula;
import org.sybila.parasim.model.verification.stl.OrFormula;
import org.sybila.parasim.model.verification.stl.TimeInterval;
import org.sybila.parasim.model.verification.stl.UntilFormula;
import org.sybila.parasim.util.Pair;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class TestFormulaStarInfo {

    private Formula target;
    private Map<Formula, Set<Integer>> unfrozen;
    private int starNumber, maxIndices;

    private static void addPair(Map<Pair<Integer, Integer>, Float> terms, int var, int star, float mult) {
        terms.put(new Pair<>(var, star), mult);
    }

    private static Set<Integer> getSet(int... values) {
        Set<Integer> result = new HashSet<>();
        for (int i = 0; i < values.length; i++) {
            result.add(values[i]);
        }
        return result;
    }

    @BeforeSuite
    public void populateStructures() {
        Map<Pair<Integer, Integer>, Float> terms;
        unfrozen = new HashMap<>();

        terms = new HashMap<>();
        addPair(terms, 0, 0, 25.21f);
        addPair(terms, 1, 2, 45.58f);
        addPair(terms, 1, 3, 81.5f);
        Formula p1 = new LinearPredicate(terms, 0.294f, LinearPredicate.Type.GREATER, new PointVariableIdentity());
        unfrozen.put(p1, getSet(2, 3));

        terms = new HashMap<>();
        addPair(terms, 0, 0, 49.79f);
        addPair(terms, 1, 1, 80.57f);
        Formula p2 = new LinearPredicate(terms, 91.2f, LinearPredicate.Type.LESSER, new PointVariableIdentity());
        unfrozen.put(p2, getSet(1));

        Formula andP1P2 = new AndFormula(p1, p2);
        unfrozen.put(andP1P2, getSet(1, 2, 3));

        Formula frz2AndP1P2 = new FreezeFormula(andP1P2, 2);
        unfrozen.put(frz2AndP1P2, getSet(1, 3));

        Formula notFrz2AndP1P2 = new NotFormula(frz2AndP1P2);
        unfrozen.put(notFrz2AndP1P2, getSet(1, 3));

        terms = new HashMap<>();
        addPair(terms, 2, 4, 95.86f);
        addPair(terms, 0, 1, 21.74f);
        Formula p3 = new LinearPredicate(terms, 80.8f, LinearPredicate.Type.EQUALS, new PointVariableIdentity());
        unfrozen.put(p3, getSet(1, 4));

        Formula gP3 = new GloballyFormula(p3, new TimeInterval(0.4f, 1.3f, IntervalBoundaryType.CLOSED));
        unfrozen.put(gP3, getSet(1, 4));

        Formula frz1GP3 = new FreezeFormula(gP3, 1);
        unfrozen.put(frz1GP3, getSet(4));

        terms = new HashMap<>();
        addPair(terms, 1, 2, 15.9f);
        addPair(terms, 1, 0, 83.54f);
        Formula p4 = new LinearPredicate(terms, 51.8f, LinearPredicate.Type.LESSER, new PointVariableIdentity());
        unfrozen.put(p4, getSet(2));

        Formula orP4Frz1GP3 = new OrFormula(p4, frz1GP3);
        unfrozen.put(orP4Frz1GP3, getSet(2, 4));

        Formula fOrP4Frz1GP3 = new FutureFormula(orP4Frz1GP3, new TimeInterval(1.2f, 8.9f, IntervalBoundaryType.CLOSED));
        unfrozen.put(fOrP4Frz1GP3, getSet(2, 4));

        Formula frz4FOrP4Frz1GP3 = new FreezeFormula(fOrP4Frz1GP3, 4);
        unfrozen.put(frz4FOrP4Frz1GP3, getSet(2));

        Formula uAll = new UntilFormula(notFrz2AndP1P2, frz4FOrP4Frz1GP3, new TimeInterval(12.8f, 25.9f, IntervalBoundaryType.CLOSED));
        unfrozen.put(uAll, getSet(1, 2, 3));

        Formula top = new FreezeFormula(uAll, 4);
        unfrozen.put(top, getSet(1, 2, 3));

        target = top;
        starNumber = 4;
        maxIndices = 3;
    }

    @Test
    public void testEquality() {
        FormulaStarInfo info = new FormulaStarInfo(target);
        Assert.assertEquals(info.getStarNumber(), starNumber, "Incorrectly computed star number.");
        Assert.assertEquals(info.getMaxUnfrozenIndices(), maxIndices, "Incorrectly maximum computed frozen indices.");
        for (Map.Entry<Formula, Set<Integer>> entry : unfrozen.entrySet()) {
            Collection<Integer> indices = info.getUnfrozenIndices(entry.getKey());
            Assert.assertNotNull(indices, "Info stores no information about a subformula.");
            System.out.println(entry.getValue());
            Assert.assertEquals(new HashSet<>(indices), entry.getValue());
        }
    }
}
