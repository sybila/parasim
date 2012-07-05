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
package org.sybila.parasim.util;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestDebug {

    @Test(enabled=false)
    public void testMemory() {
        Debug.reset();
        assertEquals(Debug.memory(), 0);
        byte[] bytes = new byte[100];
        for (byte i=0; i<bytes.length; i++) {
            bytes[i] = i;
        }
        long memory = Debug.memory();
        assertTrue(memory > 0, "Measured amout of memory should be greater than 0, was <" + memory + ">.");
        assertTrue(memory <= 200, "Measured amout of memory should be lower than 200, was <" + memory + ">.");
        for (byte i=0; i<bytes.length; i++) {
            assertEquals(i, bytes[i]);
        }

    }

    @Test
    public void testMemoryAndTome() throws InterruptedException {
        Debug.reset();
        Debug.memory();
        assertEquals(Debug.time(), 0);
        Thread.sleep(100);
        long time = Debug.time();
        assertTrue(time >= 100 * 1000 * 1000);
        assertTrue(time <= 150 * 1000 * 1000);
        assertEquals(Debug.time(), 0);
        time = Debug.time();
        assertTrue(time >= 0);
        assertTrue(time <= 10 * 1000 * 1000);
        Debug.memory();
    }

    @Test
    public void testTime() throws InterruptedException {
        assertEquals(Debug.time(), 0);
        Thread.sleep(100);
        long time = Debug.time();
        assertTrue(time >= 100 * 1000 * 1000);
        assertTrue(time <= 150 * 1000 * 1000);
        assertEquals(Debug.time(), 0);
        time = Debug.time();
        assertTrue(time >= 0 * 1000 * 1000);
        assertTrue(time <= 10 * 1000 * 1000);
    }

}
