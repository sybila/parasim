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

import junit.framework.Assert;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.computation.verification.cpu.StarMonitor;
import org.sybila.parasim.model.verification.stl.AndFormula;
import org.sybila.parasim.model.verification.stl.Formula;
import org.sybila.parasim.model.verification.stlstar.FormulaStarInfo;
import org.testng.annotations.Test;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class TestAndMonitor {

    private static final ConstantStarMonitor.Function AND_FUNCTION = new ConstantStarMonitor.Function() {

        @Override
        public float compute(float[] values) {
            Validate.notNull(values);
            Validate.isTrue(values.length > 1);
            return Math.min(values[0], values[1]);
        }
    };
    private ConstantStarMonitor phiMonitor;
    private ConstantStarMonitor psiMonitor;
    private ConstantStarMonitor destinationMonitor;

    public void computeAndTest() {
        Formula formula = new AndFormula(phiMonitor.getFormula(), psiMonitor.getFormula());
        StarMonitor result = new AndStarMonitor(formula, new FormulaStarInfo(formula), phiMonitor, psiMonitor);
        Assert.assertTrue(destinationMonitor.isEqualTo(result));
    }

    @Test
    public void testSameSize() {
        phiMonitor = ConstantStarMonitor.createLinearMonitor(8, 4, 0.5f, 1.2f, 1.5f, 4.7f);
        psiMonitor = ConstantStarMonitor.createLinearMonitor(8, 4, 0.5f, 1.2f, 30.2f, -3.2f);
        destinationMonitor = ConstantStarMonitor.computePointWiseMonitor(AND_FUNCTION, new ConstantStarMonitor[]{phiMonitor, psiMonitor});
        computeAndTest();
    }

    @Test
    public void testDifferentSizes() {
        phiMonitor = ConstantStarMonitor.createLinearMonitor(10, 4, 0f, 1f, 0.23f, 1.98f);
        psiMonitor = ConstantStarMonitor.createLinearMonitor(8, 5, 0f, 1f, 12.83f, -1.56f);
        destinationMonitor = ConstantStarMonitor.computePointWiseMonitor(AND_FUNCTION, new ConstantStarMonitor[]{phiMonitor, psiMonitor});
        computeAndTest();
    }
}
