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

import org.sybila.parasim.computation.verification.cpu.Monitor;
import org.sybila.parasim.model.verification.stl.IntervalBoundaryType;
import org.sybila.parasim.model.verification.stl.TimeInterval;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestUntilMonitor extends AbstractMonitorTest {

    @Test
    public void testUntilMonitor() {
        Monitor left = createTestMonitor(1, 2, 3, 4, 3, 2, -10);
        Monitor right = createTestMonitor(2, 1, -10, 3, 9, 0, 0);
        Monitor until = new UntilMonitor(left, right, new TimeInterval(2, 4, IntervalBoundaryType.CLOSED));
        Monitor expected = createTestMonitor(1, 2, 3);
        assertEquals(until.size(), expected.size());
        for (int i=0; i<expected.size(); i++) {
            assertEquals(until.getRobustness(i).getValue(), expected.getRobustness(i).getValue(), "The robustness doesn't match in index <" + i + ">.");
        }
    }

}
