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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.computation.verification.api.Monitor;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.model.verification.stl.Formula;
import org.sybila.parasim.model.verification.stl.Predicate;
import org.sybila.parasim.model.verification.stlstar.FormulaStarInfo;
import org.sybila.parasim.model.verification.stlstar.MultiPoint;
import org.sybila.parasim.util.Coordinate;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class TestAbstractStarMonitor {

    private static class TestingMonitor extends AbstractStarMonitor {

        private static class TestingPredicate extends Predicate {

            private final int starNum;
            private final Set<Integer> unfrozen;

            public TestingPredicate(int starNum, Set<Integer> unfrozen) {
                super(Collections.EMPTY_LIST);
                this.starNum = starNum;
                Validate.notNull(unfrozen);
                this.unfrozen = unfrozen;
            }

            @Override
            public int getStarNumber() {
                return starNum;
            }

            @Override
            public Set<Integer> getStars() {
                return unfrozen;
            }

            @Override
            public float getValue(MultiPoint mp) {
                Assert.fail("This method should not be used.");
                return Float.NaN;
            }

            @Override
            public float getValue(Point point) {
                Assert.fail("This method should not be used.");
                return Float.NaN;
            }

            @Override
            public float getValue(float[] point) {
                Assert.fail("This method should not be used.");
                return Float.NaN;
            }

            @Override
            public Predicate mergeFrozenDimensions(Set<Integer> frozen) {
                Assert.fail("This method should not be used.");
                return null;
            }

            @Override
            public Predicate substituteStars(Map<Integer, Integer> substitution) {
                Assert.fail("This method should not be used.");
                return null;
            }

            @Override
            public boolean isValid(MultiPoint mp) {
                Assert.fail("This method should not be used.");
                return false;
            }

            @Override
            public boolean isValid(Point p) {
                Assert.fail("This method should not be used.");
                return false;
            }

            @Override
            public String toString() {
                Assert.fail("This method should not be used.");
                return null;
            }
        }
        private final int size;
        private Coordinate expected;

        private TestingMonitor(int size, Formula predicate) {
            super(predicate, new FormulaStarInfo(predicate));
            this.size = size;
        }

        @Override
        protected Robustness computeRobustness(Coordinate index) {
            Assert.assertEquals(index, expected, "Abstract star monitor did not convert coordinate correctly.");
            return Robustness.UNDEFINED;
        }

        @Override
        public Collection<Monitor> getSubmonitors() {
            return Collections.EMPTY_LIST;
        }

        @Override
        public int size() {
            return size;
        }

        public void setExpected(Coordinate coord) {
            Validate.notNull(coord);
            expected = coord;
        }

        public static TestingMonitor getMonitor(int size, int starNum, Set<Integer> unfrozen) {
            int num = 0;
            for (Integer i : unfrozen) {
                num = Math.max(num, i);
            }
            if (starNum < num) {
                throw new IllegalArgumentException("Star number too low.");
            }

            return new TestingMonitor(size, new TestingPredicate(starNum, unfrozen));
        }
    }
    private TestingMonitor monitor;

    @BeforeTest
    public void createMonitor() {
        monitor = TestingMonitor.getMonitor(15, 4, new HashSet<>(Arrays.asList(new Integer[]{1, 3})));
    }

    private void testErroneousCoordinate(Coordinate coord) {
        try {
            monitor.getRobustness(coord);
            Assert.fail("Calling getRobustness(" + coord + ") should cause error.");
        } catch (IllegalArgumentException iae) {
        }
    }

    @Test
    public void testGetRobustnessIndexError() {
        try {
            monitor.getRobustness(-5);
            Assert.fail("Negative index in getRobustness should cause error.");
        } catch (IllegalArgumentException iae) {
        }
        try {
            monitor.getRobustness(15);
            Assert.fail("Index in getRobustness greater than size should cause error.");
        } catch (IllegalArgumentException iae) {
        }
        testErroneousCoordinate(new Coordinate(5, new int[]{-1, 2, 5, 14, 12}));
        testErroneousCoordinate(new Coordinate(5, new int[]{0, 15, 2, 13, 8}));
        testErroneousCoordinate(new Coordinate(5, new int[]{12, 13, 8, 5, -1}));
    }

    @Test
    public void testGetRobustnessFromIndex() {
        Coordinate.Builder coord = new Coordinate.Builder(5);
        for (int index : new int[]{2, 5, 12}) {
            coord.setCoordinate(0, index);
            monitor.setExpected(coord.create());
            monitor.getRobustness(index);
        }
    }

    @Test
    public void testGetRobustnessUnchanged() {
        Coordinate coord = new Coordinate(5, new int[]{7, 12, 0, 2, 0});
        monitor.setExpected(coord);
        monitor.getRobustness(coord);
    }

    @Test
    public void testGetRobustnessChanged() {
        Coordinate input = new Coordinate(5, new int[]{2, 3, 8, 12, 14});
        Coordinate output = new Coordinate(5, new int[]{2, 3, 0, 12, 0});
        monitor.setExpected(output);
        monitor.getRobustness(input);
    }
}
