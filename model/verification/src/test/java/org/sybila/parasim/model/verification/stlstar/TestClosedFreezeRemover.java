package org.sybila.parasim.model.verification.stlstar;

import org.sybila.parasim.model.verification.stl.Formula;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class TestClosedFreezeRemover {

    @Test
    public void testRemoveFreezes() {
        TestFormulae forms = new TestFormulae("removeFreeze.phi.xml", "removeFreeze.psi.xml");
        Formula result = ClosedFreezeRemover.removeClosedFreezes(forms.phi());
        Assert.assertTrue(FormulaEquality.equalUpToFreezeIndices(forms.psi(), result));
    }
}
