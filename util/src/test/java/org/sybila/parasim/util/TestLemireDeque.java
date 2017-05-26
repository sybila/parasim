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
package org.sybila.parasim.util;

import java.util.Comparator;
import java.util.Deque;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestLemireDeque {

    @Test
    public void testAddFirstDecreasingSequence() {
        Deque deque = new LemireDeque(createComparator());
        for (int i=10; i>0; i--) {
            deque.addFirst(i);
        }
        assertEquals(deque.size(), 1);
    }

    @Test
    public void testAddFirstIncreasingSequence() {
        Deque deque = new LemireDeque(createComparator());
        for (int i=0; i<10; i++) {
            deque.addFirst(i);
        }
        assertEquals(deque.size(), 10);
    }

    @Test
    public void testAddLastDecreasingSequence() {
        Deque deque = new LemireDeque(createComparator());
        for (int i=0; i<10; i++) {
            deque.addLast(i);
        }
        assertEquals(deque.size(), 1);
    }

    @Test
    public void testAddLastIncreasingSequence() {
        Deque deque = new LemireDeque(createComparator());
        for (int i=0; i<10; i++) {
            deque.addFirst(i);
        }
        assertEquals(deque.size(), 10);
    }

    protected Comparator<Integer> createComparator() {
        return new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        };
    }

}
