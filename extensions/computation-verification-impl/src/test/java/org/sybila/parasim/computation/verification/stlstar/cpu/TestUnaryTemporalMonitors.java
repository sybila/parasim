package org.sybila.parasim.computation.verification.stlstar.cpu;

import java.util.Arrays;
import java.util.List;
import org.sybila.parasim.computation.verification.cpu.StarMonitor;
import org.sybila.parasim.model.verification.stl.FormulaInterval;
import org.sybila.parasim.model.verification.stl.FutureFormula;
import org.sybila.parasim.model.verification.stl.GloballyFormula;
import org.sybila.parasim.model.verification.stl.IntervalBoundaryType;
import org.sybila.parasim.model.verification.stl.TemporalFormula;
import org.sybila.parasim.model.verification.stl.TimeInterval;
import org.sybila.parasim.model.verification.stlstar.FormulaStarInfo;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class TestUnaryTemporalMonitors {

    private ConstantStarMonitor sourceMonitor;
    private ConstantStarMonitor futureMonitor;
    private ConstantStarMonitor globallyMonitor;
    private FormulaInterval interval;

    @BeforeSuite
    public void createMonitors() {
        interval = new TimeInterval(1f, 5f, IntervalBoundaryType.CLOSED);
        List<Float> times = Arrays.asList(0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f);
        List<Float> shorterTimes = Arrays.asList(0f, 1f, 2f, 3f, 4f);

        sourceMonitor = new ConstantStarMonitor(times, Arrays.asList(new Float[]{
                    52.66f, 62.33f, 86.05f, 49.19f, 48.9f, 55.87f, 61.07f, 99.02f, 54.91f, 70.02f,
                    95.82f, 51.21f, 30.01f, 63.31f, 60.34f, 42.96f, 61.3f, 35.04f, 21.26f, 87.35f,
                    82.47f, 43.23f, 79.46f, 35.33f, 34.58f, 73.21f, 90.59f, 72.13f, 25.43f, 54.67f,
                    20.64f, 78.09f, 17f, 6.7f, 27.27f, 65.9f, 62.57f, 88.34f, 64.92f, 17.48f,
                    58.36f, 60.74f, 68.69f, 88.37f, 24.05f, 29.03f, 31.33f, 85.35f, 64.07f, 52.59f,
                    72.69f, 46.55f, 95.83f, 52.16f, 81.87f, 30.41f, 25.36f, 72.46f, 2.54f, 50.79f,
                    27.14f, 23.19f, 28.88f, 44.14f, 29.89f, 56.15f, 10.04f, 92.46f, 44.5f, 74.96f,
                    9.94f, 2.86f, 35.7f, 78.63f, 91.23f, 59.74f, 7.67f, 22.56f, 45.09f, 71.74f,
                    75.15f, 17.79f, 18.29f, 70.98f, 69.94f, 0.16f, 1.39f, 95.31f, 72.62f, 3.94f,
                    46.1f, 99.76f, 27.13f, 74.97f, 43.9f, 57.01f, 31.13f, 53.94f, 49.47f, 75.62f
                }));
        futureMonitor = new ConstantStarMonitor(shorterTimes, Arrays.asList(new Float[]{
                    86.05f, 86.05f, 99.02f, 99.02f, 99.02f,
                    63.31f, 63.31f, 63.31f, 61.3f, 87.35f,
                    79.46f, 90.59f, 90.59f, 90.59f, 90.59f,
                    78.09f, 65.9f, 88.34f, 88.34f, 88.34f,
                    88.37f, 88.37f, 88.37f, 85.35f, 85.35f
                }));
        globallyMonitor = new ConstantStarMonitor(shorterTimes, Arrays.asList(new Float[]{
                    48.9f, 48.9f, 48.9f, 48.9f, 54.91f,
                    30.01f, 30.01f, 35.04f, 21.26f, 21.26f,
                    34.58f, 34.58f, 34.58f, 25.43f, 25.43f,
                    6.7f, 6.7f, 6.7f, 27.27f, 17.48f,
                    24.05f, 24.05f, 24.05f, 24.05f, 29.03f
                }));
    }

    @Test
    public void testGloballyMonitor() {
        TemporalFormula formula = new GloballyFormula(sourceMonitor.getFormula(), interval);
        StarMonitor resultMonitor = UnaryTemporalStarMonitor.getGloballyMonitor(formula, new FormulaStarInfo(formula), sourceMonitor);
        Assert.assertTrue(globallyMonitor.isEqualTo(resultMonitor));
    }

    @Test
    public void testFutureMonitor() {
        TemporalFormula formula = new FutureFormula(sourceMonitor.getFormula(), interval);
        StarMonitor resultMonitor = UnaryTemporalStarMonitor.getFutureMonitor(formula, new FormulaStarInfo(formula), sourceMonitor);
        Assert.assertTrue(futureMonitor.isEqualTo(resultMonitor));
    }
}