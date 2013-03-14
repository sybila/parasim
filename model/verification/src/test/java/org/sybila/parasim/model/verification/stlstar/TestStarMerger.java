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
