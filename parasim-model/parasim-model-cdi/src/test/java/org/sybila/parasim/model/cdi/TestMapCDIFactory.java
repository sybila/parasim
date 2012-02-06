package org.sybila.parasim.model.cdi;

import java.util.ArrayList;
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
        
        public List getInjectedList() {
            return injectedList;
        }
        
        public Set getInjectedSet() {
            return injectedSet;
        }       
    }
    
    @BeforeMethod
    public void beforeMethod() {
        factory  = new MapCDIFactory();
        factory.addService(List.class, ArrayList.class);
        factory.addService(Set.class, new HashSet<Object>());
        o1 = new TestedObject();
        o2 = new TestedObject();
    }
    
    @Test
    public void testInjectFields() {
        factory.injectFields(o1);
        factory.injectFields(o2);
        assertEquals(o1.getInjectedList(), o2.getInjectedList());
        assertEquals(o1.getInjectedSet(), o2.getInjectedSet());
    }
    
}