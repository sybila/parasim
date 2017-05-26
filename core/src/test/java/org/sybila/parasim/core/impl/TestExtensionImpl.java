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
package org.sybila.parasim.core.impl;

import org.sybila.parasim.core.annotation.Application;
import org.sybila.parasim.core.annotation.Inject;
import org.sybila.parasim.core.annotation.Observes;
import org.sybila.parasim.core.annotation.Provide;
import org.sybila.parasim.core.api.Extension;
import static org.testng.Assert.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestExtensionImpl {

    private Extension extension;

    private static class TestedExtension {
        @Inject
        private String name;
        @Provide
        private String provide;
        public void observe(@Observes Object event) {
        }
        @Provide
        public Integer provide() {
            return 1;
        }
    }

    @BeforeMethod
    public void prepareExtension() throws Exception {
        extension = new ExtensionImpl(Application.class, TestedExtension.class);
    }

    @Test
    public void testObservers() {
        assertEquals(extension.getObservers().size(), 1);
    }

    @Test
    public void testInjectionPoints() {
        assertEquals(extension.getInjections().size(), 1);
    }

    @Test
    public void testProvidingPoints() {
        assertEquals(extension.getProviders().size(), 2);
    }
}
