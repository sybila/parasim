package org.sybila.parasim.core.extension.cdi;

import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.extension.AbstractExtensionTest;
import org.sybila.parasim.core.extension.cdi.api.ServiceFactory;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestServiceFactoryExtension extends AbstractExtensionTest {
    
    @Inject
    private String toInject;
    
    public static ServiceFactory serviceFactory;
    
    @Test
    public void testServiceFactory() {
        getManager().start();
        assertNotNull(serviceFactory);
        serviceFactory.injectFields(this, getManager().getRootContext());
        assertEquals(toInject, "HELLO");
    }
    
}
