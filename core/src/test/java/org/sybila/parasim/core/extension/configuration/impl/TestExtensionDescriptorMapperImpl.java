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
package org.sybila.parasim.core.extension.configuration.impl;

import java.awt.Color;
import org.sybila.parasim.core.extension.AbstractExtensionTest;
import org.sybila.parasim.core.extension.configuration.api.ExtensionDescriptorMapper;
import org.sybila.parasim.core.extension.configuration.api.ExtensionDescriptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestExtensionDescriptorMapperImpl extends AbstractExtensionTest {

    private ConfigBean configBean;
    private ExtensionDescriptor descriptor;
    public static ExtensionDescriptorMapper mapper;

    private class ConfigBean {
        private int intNumber;
        private float floatNumber;
        private boolean bool;
        private Integer[] intNumbers;
        private Color color;
    }

    @BeforeMethod
    public void prepare() {
        descriptor = new ExtensionDescriptorImpl("my-extension");
        descriptor.setProperty("intNumber", "20");
        descriptor.setProperty("floatNumber", "20.5");
        descriptor.setProperty("bool", "true");
        descriptor.setProperty("intNumbers", new String[] {"1", "2", "3"});
        descriptor.setProperty("color", "Black");
        configBean = new ConfigBean();
    }

    @Test
    public void testMap() throws IllegalAccessException {
        new ExtensionDescriptorMapperImpl().map(descriptor, configBean);
        assertEquals(configBean.bool, true);
        assertEquals(configBean.intNumber, 20);
        assertEquals(configBean.floatNumber, (float) 20.5);
        assertEquals(configBean.intNumbers, new int[] {1, 2, 3});
        assertEquals(configBean.color, Color.BLACK);
    }

    @Test
    public void testLoaded() {
        getManager().start();
        assertNotNull(mapper);
    }
}
