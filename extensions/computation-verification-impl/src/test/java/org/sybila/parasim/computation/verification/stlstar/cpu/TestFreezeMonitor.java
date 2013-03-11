package org.sybila.parasim.computation.verification.stlstar.cpu;

import java.util.Arrays;
import java.util.List;
import org.sybila.parasim.computation.verification.cpu.StarMonitor;
import org.sybila.parasim.model.verification.stl.FreezeFormula;
import org.sybila.parasim.model.verification.stlstar.FormulaStarInfo;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class TestFreezeMonitor {

    private ConstantStarMonitor sourceMonitor;
    private ConstantStarMonitor[] freezeMonitor;

    @BeforeSuite
    public void populateMonitors() {
        List<Float> values = Arrays.asList(95.22f, 14.67f, 69.84f,
                55.97f, 5f, 73.55f,
                9.51f, 85.51f, 85.62f,
                //
                76.22f, 68.78f, 0.45f,
                91.89f, 1.44f, 28.51f,
                77.88f, 84.98f, 74.76f,
                //
                58.98f, 19.52f, 73.34f,
                10.34f, 9.88f, 98.1f,
                34.13f, 46.43f, 89.54f);
        List<Float> times = Arrays.asList(0f, 1f, 2f);
        sourceMonitor = new ConstantStarMonitor(times, values);

        freezeMonitor = new ConstantStarMonitor[2];
        values = Arrays.asList(95.22f, 5f, 85.62f,
                95.22f, 5f, 85.62f,
                95.22f, 5f, 85.62f,
                //
                76.22f, 1.44f, 74.76f,
                76.22f, 1.44f, 74.76f,
                76.22f, 1.44f, 74.76f,
                //
                58.98f, 9.88f, 89.54f,
                58.98f, 9.88f, 89.54f,
                58.98f, 9.88f, 89.54f);
        freezeMonitor[0] = new ConstantStarMonitor(times, values);
        values = Arrays.asList(95.22f, 68.78f, 73.34f,
                55.97f, 1.44f, 98.1f,
                9.51f, 84.98f, 89.54f,
                //
                95.22f, 68.78f, 73.34f,
                55.97f, 1.44f, 98.1f,
                9.51f, 84.98f, 89.54f,
                //
                95.22f, 68.78f, 73.34f,
                55.97f, 1.44f, 98.1f,
                9.51f, 84.98f, 89.54f);
        freezeMonitor[1] = new ConstantStarMonitor(times, values);
    }

    public void testFreeze(int i) {
        FreezeFormula formula = new FreezeFormula(sourceMonitor.getFormula(), i + 1);
        StarMonitor resultMonitor = new FreezeMonitor(formula, new FormulaStarInfo(formula), sourceMonitor);
        Assert.assertTrue(freezeMonitor[i].isEqualTo(resultMonitor));
    }

    @Test
    public void testFreeze1() {
        testFreeze(0);
    }

    @Test
    public void testFreeze2() {
        testFreeze(1);
    }
}
