package org.sybila.parasim.model.cdi;

import java.lang.reflect.Method;
import org.sybila.parasim.model.cdi.annotations.Inject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestAbstractServiceFactory {
    
    private static final String STRING_TO_INJECT = "1234";
    
    private static final String FRESH_STRING_TO_INJECT = "FRESH 1234";
    
    private ServiceFactory cdiFactory = new AbstractServiceFactory() {

        @Override
        public Object getService(Class<?> interfaze, boolean fresh, Object... parameters) {
            if (fresh) {
                return FRESH_STRING_TO_INJECT;
            } else {
                return STRING_TO_INJECT;
            }
            
        }

        public void addService(Class<?> interfaze, Class<?> implementation) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void addService(Class<?> interfaze, Object implementation) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public boolean isServiceAvailable(Class<?> interfaze) {
            return interfaze.equals(String.class);
        }
    };
    
    private TestedObject testedObject;
    
    private class TestedObject {
        
        @Inject
        private String name;
        
        @Inject(fresh=true)
        private String freshName;
       
        private String injectedFromParam;
        
        public String getName() {
            return name;
        }
        
        public String getFreshName() {
            return freshName;
        }
        
        public void injectFromParam(String name) {
            injectedFromParam = name;
        }

    }
    
    @BeforeMethod
    public void beforeMethod() {
        testedObject = new TestedObject();
    }

    @Test
    public void testExecuteVoidMethod() throws NoSuchMethodException {
        Method method = testedObject.getClass().getMethod("injectFromParam", String.class);
        cdiFactory.executeVoidMethod(testedObject, method);
        assertEquals(testedObject.injectedFromParam, STRING_TO_INJECT);
    }
    
    @Test
    public void testInjectFields() {
        cdiFactory.injectFields(testedObject);
        assertEquals(testedObject.getName(), STRING_TO_INJECT);
    }
    
    @Test
    public void testInjectFreshFields() {
        cdiFactory.injectFields(testedObject);
        assertNotNull(testedObject.getFreshName());
        assertEquals(testedObject.getFreshName(), FRESH_STRING_TO_INJECT);
    }
    
}
