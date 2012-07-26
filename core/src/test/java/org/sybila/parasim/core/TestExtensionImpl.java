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
package org.sybila.parasim.core;

import org.sybila.parasim.core.spi.InstanceCleaner;
import java.util.ArrayList;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.annotations.Observes;
import org.sybila.parasim.core.annotations.Provide;
import org.sybila.parasim.core.context.ApplicationContext;
import org.sybila.parasim.core.event.ManagerStarted;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class TestExtensionImpl {

    private Extension extension;

    private class TestedExtension {
        @Inject
        private Instance<String> name;
        @Inject
        private Instance<String> address;
        @Inject
        private Event<String> event;
        @Provide
        private Integer staticNumber = 1;
        private  long counter = 1;
        public void observe(@Observes ManagerStarted event) {}
        @Provide
        public Long getDynamicNumber() {
            return counter;
        }
    }

    @BeforeMethod
    public void prepareExtension() {
        extension = new ExtensionImpl(new TestedExtension(), new ApplicationContext());
    }

    @Test
    public void testObservers() {
        assertEquals(extension.getObservers().size(), 1);
    }

    @Test
    public void testInjectionPoints() {
        assertEquals(extension.getInjectionPoints().size(), 2);
    }

    @Test
    public void testEventPoints() {
        assertEquals(extension.getEventPoints().size(), 1);
    }

    @Test
    public void testProvidingPoints() {
        assertEquals(extension.getProvidingPoints().size(), 2);
    }
}
