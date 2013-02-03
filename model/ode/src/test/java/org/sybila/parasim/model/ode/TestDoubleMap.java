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
package org.sybila.parasim.model.ode;


import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestDoubleMap { 
    private static Map<String,String> map;
    
    @BeforeMethod
    public void fillData() {
        map = new HashMap<String, String>();
        map.put("a", "x");
        map.put("b", "y");
        map.put("c", "z");
    }
    
    private DoubleMap<String> newFullMapUsingPut() {
        DoubleMap<String> target = new DoubleMap<String>();
        for (Map.Entry<String, String> e : map.entrySet()) {
            if (!target.put(e.getKey(), e.getValue())) {
                fail("Method put did not allow to put injective mapping.");
            }
        }
        return target;
    }
    
    private DoubleMap<String> newFullMapUsingConstructor() {
        return new DoubleMap<String>(map);
    }
    
    private void testContainsKeys(DoubleMap<String> target) {
        for (String key : map.keySet()) {
            assertTrue(target.containsKey(key), "Target should contain inserted key.");
        }
    }
    
    private void testContainsValues(DoubleMap<String> target) {
        for (String value : map.values()) {
            assertTrue(target.containsName(value), "Target should contain inserted name");
        }
    }
    
    private void testGetName(DoubleMap<String> target) {
        for (Map.Entry<String,String> e : map.entrySet()) {
            assertEquals(target.getName(e.getKey()), e.getValue(), "Variable key associated with wrong name.");
        }
    }
    
    private void testGetKey(DoubleMap<String> target) {
        for (Map.Entry<String, String> e : map.entrySet()) {
            assertEquals(target.getKey(e.getValue()), e.getKey(), "Variable name associated with wrong key.");
        }
    }
    
     
    /**
     * Tests whether after putting in a pair using {@link DoubleMap#put}, the DoubleMap contains it.
     */
    @Test
    public void testPutContains() {
        DoubleMap<String> test = newFullMapUsingPut();
        testContainsKeys(test);
        testContainsValues(test);
    }
    
    /**
     * Tests whether after putting in a pair using constructor, the DoubleMap contains it.
     */
    @Test
    public void testConstructorContains() {
        DoubleMap<String> test = newFullMapUsingConstructor();
        testContainsKeys(test);
        testContainsValues(test);
    }
    /**
     * Tests whether after putting in a pair using {@link DoubleMap#put}, the keys are associated correctly.
     */
    @Test
    public void testPutGet() {
        DoubleMap<String> test = newFullMapUsingPut();
        testGetKey(test);
        testGetName(test);
    }
    
    /**
     * Tests whether after putting in a pair using constructor, the keys are associated correctly.
     */
    @Test
    public void testConstructorGet() {
        DoubleMap<String> test = newFullMapUsingConstructor();
        testGetKey(test);
        testGetName(test);
    }
    
    
}
