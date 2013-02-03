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
package org.sybila.parasim.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestIterables {

    @Test
    public void testConcat() {
        List<Integer> first = Arrays.asList(1, 2);
        List<Integer> second = Arrays.asList(3, 4, 5);
        List<Integer> third = Collections.EMPTY_LIST;
        List<Integer> fourth = Collections.EMPTY_LIST;
        List<Integer> fifth = Arrays.asList(6, 7, 8, 9, 10);
        List<Integer> sixth = Collections.EMPTY_LIST;
        Iterator<Integer> result = Iterables.concat(first, second, third, fourth, fifth, sixth).iterator();
        for (int i=1; i<=10; i++) {
            Integer found = result.next();
            Assert.assertNotNull(found);
            Assert.assertEquals(found, new Integer(i));
        }
    }

}
