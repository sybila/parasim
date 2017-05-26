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
package org.sybila.parasim.computation.verification.stlstar.cpu;

import java.util.Arrays;
import java.util.List;
import org.sybila.parasim.computation.verification.cpu.StarMonitor;
import org.sybila.parasim.model.verification.stl.FormulaInterval;
import org.sybila.parasim.model.verification.stl.IntervalBoundaryType;
import org.sybila.parasim.model.verification.stl.TimeInterval;
import org.sybila.parasim.model.verification.stl.UntilFormula;
import org.sybila.parasim.model.verification.stlstar.FormulaStarInfo;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class TestUntilMonitor {

    private FormulaInterval interval;
    private ConstantStarMonitor phiMonitor;
    private ConstantStarMonitor psiMonitor;
    private ConstantStarMonitor untilMonitor;

    @BeforeSuite
    public void createMonitors() {
        interval = new TimeInterval(2f, 5f, IntervalBoundaryType.CLOSED);
        List<Float> times = Arrays.asList(new Float[]{0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f});
        phiMonitor = new ConstantStarMonitor(Arrays.asList(new Float[]{0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f}), Arrays.asList(new Float[]{
                    53.25f, 8.67f, 65.24f, 45.89f, 72.78f, 20.67f, 18.13f, 43.54f, 33.61f, 67.5f,
                    4.07f, 88.13f, 40.93f, 9.23f, 11.45f, 86.92f, 54.02f, 44.11f, 95.33f, 77.88f,
                    63.07f, 48.9f, 50.25f, 40.44f, 6.03f, 83.45f, 77.51f, 57.3f, 86.96f, 98.97f,
                    82.94f, 40.21f, 7.65f, 48.18f, 86.1f, 80.43f, 68.85f, 4.23f, 23.97f, 2.46f,
                    71.72f, 28.04f, 90.59f, 12.65f, 37.28f, 2.04f, 99.57f, 91.29f, 46.14f, 94.9f,
                    69.17f, 9.22f, 43.8f, 19.42f, 49.66f, 49.82f, 2.88f, 27.17f, 7.13f, 89.84f,
                    26.15f, 90.07f, 30.05f, 33.79f, 38.25f, 16.15f, 14.22f, 7.1f, 20.37f, 38.19f,
                    9.56f, 92.1f, 66.24f, 0.15f, 4.75f, 3.51f, 2.19f, 4.32f, 94.8f, 48.34f,
                    99.23f, 63.97f, 57.55f, 43.02f, 83.39f, 7.22f, 92.85f, 86.27f, 34.39f, 99.97f,
                    76.11f, 60.53f, 90.04f, 6.16f, 94.33f, 28.29f, 22.31f, 8.55f, 35.39f, 42.68f
                }));
        psiMonitor = new ConstantStarMonitor(Arrays.asList(new Float[]{0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f}), Arrays.asList(new Float[]{
                    83.43f, 6.82f, 27.34f, 6.11f, 91.79f, 84.24f, 63.53f, 6.65f, 6.46f,
                    54.49f, 77.83f, 96.25f, 15.14f, 2.62f, 55.35f, 62.12f, 33.97f, 4.27f,
                    68.68f, 54.56f, 97.87f, 19.6f, 44.87f, 0.02f, 29.33f, 80.3f, 38.57f,
                    53.31f, 98.59f, 43.81f, 65.08f, 79.58f, 50.63f, 92.42f, 85.68f, 42.42f,
                    56.67f, 49.22f, 49.07f, 83.11f, 47.22f, 26.9f, 79.37f, 62.36f, 29.52f,
                    75.76f, 24.49f, 63.49f, 38.98f, 47.04f, 18.05f, 36.85f, 66.64f, 62.92f,
                    65.61f, 95.98f, 43.22f, 75.44f, 44.69f, 41.82f, 19.25f, 9.77f, 21.39f,
                    35.51f, 2.18f, 7.08f, 12.3f, 78.84f, 56.29f, 61.37f, 61.95f, 3.52f,
                    32.05f, 41.32f, 65.88f, 17.78f, 76.03f, 90.37f, 81.27f, 15.02f, 37.41f
                }));
        untilMonitor = new ConstantStarMonitor(Arrays.asList(new Float[]{0f, 1f, 2f, 3f}), Arrays.asList(new Float[]{
                    8.67f, 8.67f, 45.89f, 20.67f,
                    4.07f, 9.23f, 9.23f, 9.23f,
                    48.9f, 19.6f, 6.03f, 6.03f,
                    7.65f, 7.65f, 7.65f, 48.18f
                }));
    }

    @Test
    public void testUntilMonitor() {
        UntilFormula formula = new UntilFormula(phiMonitor.getFormula(), psiMonitor.getFormula(), interval);
        StarMonitor resultMonitor = new UntilStarMonitor(formula, new FormulaStarInfo(formula), phiMonitor, psiMonitor);
        Assert.assertTrue(untilMonitor.isEqualTo(resultMonitor));
    }
}
