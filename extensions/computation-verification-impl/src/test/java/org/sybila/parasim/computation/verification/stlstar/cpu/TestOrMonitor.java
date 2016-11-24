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
