package org.sybila.parasim.model.cdi;

import org.sybila.parasim.model.cdi.annotations.Inject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestAbstractCDIFactory {
    
    private static final String STRING_TO_INJECT = "HELLO";
    
    private CDIFactory cdiFactory = new AbstractCDIFactory() {

        @Override
        protected Object getService(Class<?> interfaze) {
            return STRING_TO_INJECT;
        }

        public void addService(Class<?> interfaze, Class<?> implementation) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void addService(Class<?> interfaze, Object implementation) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    };
    
    private TestedObject testedObject;
    
    private class TestedObject {
        
        @Inject
        private String name;
        
        public String getName() {
            return name;
        }
    }
    
    @BeforeMethod
    public void beforeMethod() {
        testedObject = new TestedObject();
    }
    
    @Test
    public void testInjectFields() {
        cdiFactory.injectFields(testedObject);
        assertEquals(testedObject.getName(), STRING_TO_INJECT);
    }
    
}
