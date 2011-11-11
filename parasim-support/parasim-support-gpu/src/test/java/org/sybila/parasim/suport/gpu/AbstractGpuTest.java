package org.sybila.parasim.suport.gpu;

import org.testng.annotations.BeforeClass;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class AbstractGpuTest {
    
    private GpuContext context;
    
    @BeforeClass(alwaysRun=true)
    public void prepareContext() {
        context = new GpuContext();
    }
    
    protected GpuContext getContext() {
        return context;
    }
    
}
