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
package org.sybila.parasim.core.extension.loader;

import org.sybila.parasim.core.extension.AbstractExtensionTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestExtensionLoaderExtension extends AbstractExtensionTest {
    
    public static long managerStarted;
    public static long extensionRegistered;
    
    @BeforeMethod(groups="beforeManager")
    public void prepare() {
        managerStarted = 0;
        extensionRegistered = 0;
    }
    
    @Test
    public void testLoad() {
        getManager().start();
        getManager().shutdown();
        assertNotEquals((long) 0, extensionRegistered);
        assertNotEquals((long) 0, managerStarted);
        assertTrue(extensionRegistered < managerStarted);
    }
    
}