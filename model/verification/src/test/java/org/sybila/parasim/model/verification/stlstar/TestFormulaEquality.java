package org.sybila.parasim.model.verification.stlstar;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class TestFormulaEquality {

    @Test
    public void testFormulaeEqual() {
        TestFormulae forms = new TestFormulae("testEquality1.phi.xml", "testEquality1.psi.xml");
        Assert.assertTrue(FormulaEquality.equalUpToFreezeIndices(forms.phi(), forms.psi()));
    }

    @Test
    public void testFormulaeNotEqual() {
        TestFormulae forms = new TestFormulae("testEquality2.phi.xml", "testEquality1.psi.xml");
        Assert.assertFalse(FormulaEquality.equalUpToFreezeIndices(forms.phi(), forms.psi()));
    }
}
