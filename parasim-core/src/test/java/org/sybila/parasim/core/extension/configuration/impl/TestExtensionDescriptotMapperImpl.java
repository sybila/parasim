package org.sybila.parasim.core.extension.configuration.impl;

import org.sybila.parasim.core.extension.configuration.api.ExtensionDescriptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestExtensionDescriptotMapperImpl {
    
    private ConfigBean configBean;
    private ExtensionDescriptor descriptor;
    
    private class ConfigBean {
        private int intNumber;
        private float floatNumber;
        private boolean bool;
        private Integer[] intNumbers;
    }
    
    @BeforeMethod
    public void prepare() {
        descriptor = new ExtensionDescriptorImpl("my-extension");
        descriptor.setProperty("intNumber", "20");
        descriptor.setProperty("floatNumber", "20.5");
        descriptor.setProperty("bool", "true");
        descriptor.setProperty("intNumbers", new String[] {"1", "2", "3"});
        configBean = new ConfigBean();
    }
    
    @Test
    public void testMap() throws IllegalAccessException {
        new ExtensionDescriptorMapperImpl().map(descriptor, configBean);
        assertEquals(configBean.bool, true);
        assertEquals(configBean.intNumber, 20);
        assertEquals(configBean.floatNumber, (float) 20.5);
        assertEquals(configBean.intNumbers, new int[] {1, 2, 3});
    }
    
}
