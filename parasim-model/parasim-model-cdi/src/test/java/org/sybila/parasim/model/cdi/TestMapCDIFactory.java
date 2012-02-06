package org.sybila.parasim.model.cdi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.sybila.parasim.model.cdi.annotations.Inject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestMapCDIFactory {
    
    private CDIFactory factory;
    
    private TestedObject o1;
    private TestedObject o2;
    
    private class TestedObject {

        @Inject
        private List injectedList;
        
        @Inject
        private Set injectedSet;
        
        @Inject(fresh=true)
        private Object freshInjectedObject;
        
        @Inject(fresh=true, parameters={"injectedSet"})
        private List freshInjectedListWithParams;
        
    }
    
    @BeforeMethod
    public void beforeMethod() {
        factory  = new MapCDIFactory();
        factory.addService(List.class, ArrayList.class);
        factory.addService(Set.class, new HashSet<Object>(Arrays.asList(new Object[] {new Object()})));
        factory.addService(Object.class, Object.class);
        factory.addService(null, null);
        o1 = new TestedObject();
        o2 = new TestedObject();
    }
    
    @Test
    public void testInjectFields() {
        factory.injectFields(o1);
        factory.injectFields(o2);
        assertEquals(o1.injectedList, o2.injectedList);
        assertEquals(o1.injectedSet, o2.injectedSet);
    }
    
    @Test
    public void testIjectFreshFields() {
        factory.injectFields(o1);
        factory.injectFields(o2);
        assertNotNull(o1.freshInjectedObject);
        assertNotEquals(o1.freshInjectedObject, o2.freshInjectedObject);
    }
    
    @Test
    public void testInjectFreshFieldsWithParams() {
        factory.injectFields(o1);
        assertNotNull(o1.freshInjectedListWithParams);
        assertEquals(o1.freshInjectedListWithParams.size(), 1);
    }
    
}