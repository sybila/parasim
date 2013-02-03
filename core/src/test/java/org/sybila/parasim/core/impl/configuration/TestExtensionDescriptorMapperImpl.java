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
package org.sybila.parasim.core.impl.configuration;

import org.sybila.parasim.core.impl.configuration.ExtensionDescriptorMapperImpl;
import org.sybila.parasim.core.impl.configuration.ExtensionDescriptorImpl;
import java.awt.Color;
import java.io.File;
import java.util.concurrent.TimeUnit;
import org.sybila.parasim.core.annotation.Default;
import org.sybila.parasim.core.api.configuration.ExtensionDescriptor;
import org.sybila.parasim.core.api.configuration.ExtensionDescriptorMapper;
import org.sybila.parasim.core.test.ParasimTest;
import static org.testng.Assert.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestExtensionDescriptorMapperImpl extends ParasimTest {

    private ConfigBean configBean;
    private ExtensionDescriptor descriptor;
    public static ExtensionDescriptorMapper mapper;

    private class ConfigBean {
        private int intNumber;
        private float floatNumber;
        private boolean bool;
        private Integer[] intNumbers;
        private Color color;
        private TimeUnit unit;
        private File file;
    }

    @BeforeMethod
    public void prepare() {
        descriptor = new ExtensionDescriptorImpl("my-extension");
        descriptor.setProperty("intNumber", "20");
        descriptor.setProperty("floatNumber", "20.5");
        descriptor.setProperty("bool", "true");
        descriptor.setProperty("intNumbers", new String[] {"1", "2", "3"});
        descriptor.setProperty("color", "Black");
        descriptor.setProperty("unit", "hours");
        descriptor.setProperty("file", "/tmp");
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
        assertEquals(configBean.unit, TimeUnit.HOURS);
        assertEquals(configBean.file, new File("/tmp"));
    }

    @Test
    public void testLoaded() {
        assertNotNull(getManager().resolve(ExtensionDescriptorMapper.class, Default.class));
    }
}
