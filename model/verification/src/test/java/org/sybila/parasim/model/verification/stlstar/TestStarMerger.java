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

import org.sybila.parasim.model.verification.stl.Formula;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class TestStarMerger {

    @Test
    public void testRemoveFreezes() {
        TestFormulae forms = new TestFormulae("downShaker.phi.xml", "downShaker.xi.xml");

        Formula result = StarMerger.mergeStars(forms.phi());
        Assert.assertTrue(FormulaEquality.equalUpToFreezeIndices(forms.psi(), result));

        FormulaStarInfo psiInfo = new FormulaStarInfo(forms.psi());
        FormulaStarInfo resultInfo = new FormulaStarInfo(result);


        Assert.assertEquals(resultInfo.getStarNumber(), psiInfo.getStarNumber(), "Different star number.");
        Assert.assertEquals(resultInfo.getMaxUnfrozenIndices(), psiInfo.getMaxUnfrozenIndices(), "Different maximum unfrozen indices.");
    }
}
