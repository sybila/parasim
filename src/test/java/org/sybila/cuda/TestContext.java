package org.sybila.cuda;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestContext {
    
    @Test
    public void testIsCudaAvailable() {
        assertFalse(new Context().isAvailable());
    }
    
    @Test
    public void testGetDevices() {
        System.out.println(new Context().getDevices());
    }
    
}
