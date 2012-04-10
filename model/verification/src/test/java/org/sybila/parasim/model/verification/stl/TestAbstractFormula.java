/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.model.verification.stl;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.Arrays;

import org.testng.annotations.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Tests equals() and hashCode() of {@link AbstractFormula}.
 * 
 * 
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class TestAbstractFormula {
    private static final FormulaType TYPE = FormulaType.AND;

    private class IllegalUseException extends RuntimeException {
        public IllegalUseException(String message) {
            super(message);
        }
    }

    private class NaryFormula extends AbstractFormula {
        private Formula[] subf;
        private FormulaType type;

        public NaryFormula(Formula[] subformulae, FormulaType type) {
            if (subformulae != null) {
                subf = Arrays.copyOf(subformulae, subformulae.length);
            } else {
                subf = null;
            }
            this.type = type;
        }

        public NaryFormula(Formula subformula) {
            subf = new Formula[1];
            subf[0] = subformula;
            type = TYPE;
        }

        @Override
        public int getArity() {
            if (subf != null) {
                return subf.length;
            } else {
                return 0;
            }
        }

        @Override
        public FormulaType getType() {
            return type;
        }

        @Override
        public Formula getSubformula(int index) {
            return subf[index];
        }

        @Override
        public float getTimeNeeded() {
            throw new IllegalUseException("Time");
        }
    }

    private class SimpleFormula implements Formula {
        private int num;

        public SimpleFormula(int number) {
            num = number;
        }

        @Override
        public int getArity() {
            throw new IllegalUseException("Arity");
        }

        @Override
        public FormulaType getType() {
            throw new IllegalUseException("Type");
        }

        @Override
        public Formula getSubformula(int index) {
            throw new IllegalUseException("Subformula");
        }

        @Override
        public float getTimeNeeded() {
            throw new IllegalUseException("Time");
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SimpleFormula))
                return false;
            return (num == ((SimpleFormula) obj).num);
        }

        @Override
        public int hashCode() {
            return num;
        }

        @Override
        public Element toXML(Document doc) {
            throw new UnsupportedOperationException();
        }
    }

    private class SimpleUnaryFormula implements Formula {
        Formula phi;

        public SimpleUnaryFormula(Formula phi) {
            this.phi = phi;
        }

        @Override
        public int getArity() {
            return 1;
        }

        @Override
        public Formula getSubformula(int index) {
            return phi;
        }

        @Override
        public float getTimeNeeded() {
            throw new IllegalUseException("Time");
        }

        @Override
        public FormulaType getType() {
            return TYPE;
        }

        @Override
        public Element toXML(Document doc) {
            throw new UnsupportedOperationException();
        }
    }

    private Formula[] getFormulaList(int number) {
        Formula[] result = new Formula[number];
        for (int i = 0; i < number; i++) {
            result[i] = new SimpleFormula(i);
        }
        return result;
    }

    @Test
    public void testEquality() {
        try {
            /* 0-ary formulae */
            for (FormulaType type : FormulaType.values()) {
                Formula phi = new NaryFormula(null, type);
                Formula psi = new NaryFormula(null, type);
                assertEquals(phi, psi,
                        "0-ary formula of the same type should be equal");
            }
        } catch (IllegalUseException iue) {
            fail(iue.getMessage() + " used to discern equality.");
        }

        final int n = 17;
        Formula[] formList = getFormulaList(n);
        try {
            for (FormulaType type : FormulaType.values()) {
                Formula phi = new NaryFormula(formList, type);
                Formula psi = new NaryFormula(formList, type);
                assertEquals(phi, psi,
                        "N-ary formulae of the same type and subformulae should be equal.");
            }
        } catch (IllegalUseException iue) {
            fail("Method of subformula other than equals used during execution.");
        }
    }

    @Test
    public void testUnequality() {
        /* 0-ary of different type */
        for (FormulaType type1 : FormulaType.values()) {
            for (FormulaType type2 : FormulaType.values()) {
                if (!type1.equals(type2)) {
                    assertFalse(

                    new NaryFormula(null, type1).equals(new NaryFormula(null,
                            type2)),
                            "Formulae of different type should not be equal.");
                }
            }
        }

        /* different subformulae */
        final int n = 13;
        Formula[] list = getFormulaList(n);
        Formula phi = new NaryFormula(list, TYPE);
        for (int i = 0; i < n; i++) {
            list[i] = new SimpleFormula((i ^ 5) % n + n);
            assertFalse(

            phi.equals(new NaryFormula(list, TYPE)),
                    "Formulae with different subformulae should not be equal.");
            list[i] = new SimpleFormula(i);
        }
    }

    @Test
    public void testExtremeValues() {
        Formula target = new NaryFormula(null, TYPE);
        assertFalse(target.equals(null), "equals(null) should return false");
        assertFalse(target.equals(new Object()),
                "equals() invoked on a non-Formula should return false");

        /* agains a non-Abstract Formula, but equal formula */
        Formula phi = new SimpleFormula(1);
        target = new NaryFormula(phi);
        Formula obj = new SimpleUnaryFormula(phi);
        assertTrue(
                target.equals(obj),
                "AbstractFormula should be equal to general Formula with the same type and subformulae.");
    }

    @Test
    public void testHashCode() {
        try {
            /* 0-ary formulae */
            for (FormulaType type : FormulaType.values()) {
                Formula phi = new NaryFormula(null, type);
                Formula psi = new NaryFormula(null, type);
                assertEquals(

                phi.hashCode(), psi.hashCode(),
                        "0-ary formulae of the same type should have the same hash code.");
            }

            /* n-ary formulae */
            final int n = 15;
            Formula[] formList = getFormulaList(n);
            for (FormulaType type : FormulaType.values()) {
                Formula phi = new NaryFormula(formList, type);
                Formula psi = new NaryFormula(formList, type);
                assertEquals(
                        phi.hashCode(),
                        psi.hashCode(),
                        "n-ary formulae of the same type with the same subformulae should have the same hash code.");
            }
        } catch (IllegalUseException iue) {
            fail(iue.getMessage() + " used to compute hash code.");
        }
    }

}
