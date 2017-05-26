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

import java.util.HashSet;
import java.util.Set;
import org.sybila.parasim.model.verification.stl.Formula;
import org.sybila.parasim.model.verification.stl.FreezeFormula;
import org.sybila.parasim.model.verification.stl.TemporalFormula;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class TestStarDownShaker {

    private boolean testEquality(Formula phi, Formula psi, Set<Integer> phiIndices, Set<Integer> psiIndices) {
        if (phi.getArity() != psi.getArity()) {
            return false;
        }
        if (phi.getType() != psi.getType()) {
            return false;
        }
        switch (phi.getType()) {
            case FREEZE:
                phiIndices.add(((FreezeFormula) phi).getFreezeIndex());
                psiIndices.add(((FreezeFormula) psi).getFreezeIndex());
                break;
            case UNTIL:
            case GLOBALLY:
            case FUTURE:
                if (!((TemporalFormula) phi).getInterval().equals(((TemporalFormula) psi).getInterval())) {
                    return false;
                }
                if (!phiIndices.equals(psiIndices)) {
                    return false;
                }
                phiIndices.clear();
                psiIndices.clear();
                break;
            case PREDICATE:
                return phi.equals(psi);
        }

        for (int i = 0; i < phi.getArity(); i++) {
            if (!testEquality(phi.getSubformula(i), psi.getSubformula(i), new HashSet<>(phiIndices), new HashSet<>(psiIndices))) {
                return false;
            }
        }
        return true;
    }

    @Test
    public void testRemoveFreezes() {
        TestFormulae forms = new TestFormulae("downShaker.phi.xml", "downShaker.psi.xml");
        Formula result = StarDownShaker.INSTANCE.downShake(forms.phi());
        Assert.assertTrue(testEquality(forms.psi(), result, new HashSet<Integer>(), new HashSet<Integer>()));
    }
}
