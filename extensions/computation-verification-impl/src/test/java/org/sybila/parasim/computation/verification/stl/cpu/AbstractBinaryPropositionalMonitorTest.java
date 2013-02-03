/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
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
import static org.testng.Assert.*;


/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractBinaryPropositionalMonitorTest extends AbstractMonitorTest {

    public void testMonitor() {
        Monitor left = createIncreasingTestMonitor(10, 1);
        Monitor right = createIncreasingTestMonitor(12, 2);

        Monitor tested = createMonitor(left, right);
        assertEquals(tested.size(), left.size());
        for (int i=0; i < left.size(); i++) {
            assertEquals(tested.getRobustness(i).getValue(), function(left.getRobustness(i).getValue(), right.getRobustness(i).getValue()));
        }
    }

    abstract protected Monitor createMonitor(Monitor left, Monitor right);

    abstract protected float function(float left, float right);
}
