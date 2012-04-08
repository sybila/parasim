package org.sybila.parasim.core.extension.cdi;

import org.sybila.parasim.core.annotations.Provide;
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
    @Inject
    private Number providedNumber;
    @Provide
    private Integer toProvide = 10;
    public static ServiceFactory serviceFactory;
    public int counter = 0;
    
    @Test
    public void testServiceFactory() {
        getManager().start();
        assertNotNull(serviceFactory);
        serviceFactory.provideFieldsAndMethods(this, getManager().getRootContext());
        serviceFactory.injectFields(this, getManager().getRootContext());
        assertEquals(toInject, "HELLO");
        assertEquals(getManager().resolve(Integer.class, getManager().getRootContext()), toProvide);
    }
    
    @Test
    public void testServiceFactoryWithFreshProvider() {
        getManager().start();
        serviceFactory.provideFieldsAndMethods(this, getManager().getRootContext());
        serviceFactory.injectFields(this, getManager().getRootContext());
        for (int i=0; i<10; i++) {
            assertEquals(providedNumber.get(), i);
        }
    }
    
    @Provide(fresh=true)
    private Number provider() {
        final int x = counter++;
        return new Number() {
            public int get() {
                return x;
            }
        };
    }

}

interface Number {
    
    int get();
}