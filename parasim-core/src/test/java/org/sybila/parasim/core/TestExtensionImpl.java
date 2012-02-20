package org.sybila.parasim.core;

import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.annotations.Observes;
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
        
        public void observe(@Observes ManagerStarted event) {}
    }
    
    @BeforeMethod
    public void prepareExtension() {
        extension = new ExtensionImpl(new TestedExtension());
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
}
