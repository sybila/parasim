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
package org.sybila.parasim.model.ode;

import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.Point;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestDefaultOdeSystem {
    
    OdeSystem system;
    
    @BeforeMethod
    public void setUp() {
        system = new DefaultOdeSystem(
            new ArrayOdeSystemEncoding(
                new int[] {0, 2, 3},
                new float[] {(float)0.1, (float) 0.2, (float) 0.3},
                new int[] {0, 1, 3, 3},
                new int[] {0, 1, 1}
            )
        );
    }
    
    @Test
    public void testValue() {
        Point point = new ArrayPoint(0, 5, 10);
        assertEquals(system.value(point, 0), (float) 20.5);
        assertEquals(system.value(point, 1), (float) 0.3);
    }
    
}
