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
package org.sybila.parasim.core.extension.lifecycle;

import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.core.extension.AbstractExtensionTest;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestLifecycleExtension extends AbstractExtensionTest {

    public static int destroyedCounter = 0;

    @Test
    public void testConstructor() {
        getManager().start();
        assertNotNull(getManager().resolve(Integer.class, Default.class, getManager().getRootContext()));
    }

    @Test
    public void testDestructor() {
        int before = destroyedCounter;
        getManager().start();
        assertNotNull(getManager().resolve(Integer.class, Default.class, getManager().getRootContext()));
        getManager().shutdown();
        assertEquals(destroyedCounter, before + 1);
    }
}
