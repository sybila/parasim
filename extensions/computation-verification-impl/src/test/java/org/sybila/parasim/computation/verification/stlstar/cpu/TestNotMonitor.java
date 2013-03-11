package org.sybila.parasim.computation.verification.stlstar.cpu;

import java.util.Arrays;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.computation.verification.cpu.StarMonitor;
import org.sybila.parasim.model.verification.stl.Formula;
import org.sybila.parasim.model.verification.stl.NotFormula;
import org.sybila.parasim.model.verification.stlstar.FormulaStarInfo;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class TestNotMonitor {

    private static final ConstantStarMonitor.Function NOT_FUNCTION = new ConstantStarMonitor.Function() {

        @Override
        public float compute(float[] values) {
            Validate.notNull(values);
            Validate.isTrue(values.length > 0);
            return -values[0];
        }
    };
    private ConstantStarMonitor sourceMonitor;
    private ConstantStarMonitor destinationMonitor;

    private void computeAndTestEquality() {
        Formula formula = new NotFormula(sourceMonitor.getFormula());
        StarMonitor monitor = new NotStarMonitor(formula, new FormulaStarInfo(formula), sourceMonitor);
        Assert.assertTrue(destinationMonitor.isEqualTo(monitor));
    }

    @Test
    public void testOnSimpleMonitor() {
        sourceMonitor = new ConstantStarMonitor(Arrays.asList(new Float[]{0f, 1f, 2f}),
                Arrays.asList(new Float[]{2f, 5.1f, 9.25f, 2.51f, 36.15f, 8.26f, 5.86f, 7.25f, 83.12f}));
        destinationMonitor = new ConstantStarMonitor(Arrays.asList(new Float[]{0f, 1f, 2f}),
                Arrays.asList(new Float[]{-2f, -5.1f, -9.25f, -2.51f, -36.15f, -8.26f, -5.86f, -7.25f, -83.12f}));
        computeAndTestEquality();
    }

    @Test
    public void testOnComplexMonitor() {
        sourceMonitor = ConstantStarMonitor.createLinearMonitor(10, 4, 0f, 0.5f, 0.8f, 12.53f);
        destinationMonitor = ConstantStarMonitor.computePointWiseMonitor(NOT_FUNCTION, new ConstantStarMonitor[]{sourceMonitor});
    }
}
