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
package org.sybila.parasim.computation.verification.stl.cpu;

import org.sybila.parasim.computation.verification.api.Monitor;
import org.sybila.parasim.model.verification.stl.IntervalBoundaryType;
import org.sybila.parasim.model.verification.stl.TimeInterval;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestGloballyMonitor extends AbstractMonitorTest {

    @Test
    public void testGloballyMonitorIncreasing() {
        // 0 1 2 3 4 5 6 7 8 9
        Monitor subMonitor = createIncreasingTestMonitor(10, 1);
        Monitor future = new GloballyMonitor(EMPTY_PROPERTY, subMonitor, new TimeInterval(2, 4, IntervalBoundaryType.CLOSED));
        Monitor expected = createTestMonitor(2, 3, 4, 5, 6, 7);
        assertEquals(future.size(), expected.size(), "The monitor size doesn't match.");
        for (int i=0; i<expected.size(); i++) {
            assertEquals(future.getRobustness(i).getValue(), expected.getRobustness(i).getValue(), "The robustness doesn't match in iteration <" + i + ">,");
        }
    }

    @Test
    public void testGloballyMonitorDecreasing() {
        // 9 8 7 6 5 4 3 2 1 0
        Monitor subMonitor = createDecreasingTestMonitor(10, 1);
        Monitor future = new GloballyMonitor(EMPTY_PROPERTY, subMonitor, new TimeInterval(2, 4, IntervalBoundaryType.CLOSED));
        Monitor expected = createTestMonitor(5, 4, 3, 2, 1, 0);
        assertEquals(future.size(), expected.size(), "The monitor size doesn't match.");
        for (int i=0; i<expected.size(); i++) {
            assertEquals(future.getRobustness(i).getValue(), expected.getRobustness(i).getValue(), "The robustness doesn't match in iteration <" + i + ">,");
        }
    }

}
