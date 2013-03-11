package org.sybila.parasim.computation.verification.stlstar.cpu;

import org.apache.commons.lang3.Validate;
import org.sybila.parasim.computation.verification.cpu.StarMonitor;
import org.sybila.parasim.model.verification.stl.Formula;
import org.sybila.parasim.model.verification.stl.OrFormula;
import org.sybila.parasim.model.verification.stlstar.FormulaStarInfo;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class TestOrMonitor {

    private static final ConstantStarMonitor.Function OR_FUNCTION = new ConstantStarMonitor.Function() {

        @Override
        public float compute(float[] values) {
            Validate.notNull(values);
            Validate.isTrue(values.length > 1);
            return Math.max(values[0], values[1]);
        }
    };
    private ConstantStarMonitor phiMonitor;
    private ConstantStarMonitor psiMonitor;
    private ConstantStarMonitor destinationMonitor;

    @BeforeTest
    public void createMonitors() {
        phiMonitor = ConstantStarMonitor.createLinearMonitor(9, 4, 0f, 1f, 0.29f, 1.2f);
        psiMonitor = ConstantStarMonitor.createLinearMonitor(10, 5, 0f, 1f, 25.8f, -0.8f);
        destinationMonitor = ConstantStarMonitor.computePointWiseMonitor(OR_FUNCTION, new ConstantStarMonitor[]{phiMonitor, psiMonitor});
    }

    @Test
    public void testMonitor() {
        Formula formula = new OrFormula(phiMonitor.getFormula(), psiMonitor.getFormula());
        StarMonitor result = new OrStarMonitor(formula, new FormulaStarInfo(formula), phiMonitor, psiMonitor);
        Assert.assertTrue(destinationMonitor.isEqualTo(result));
    }
}
