package org.sybila.parasim.suport.gpu.model.ode;

import java.util.Arrays;
import org.sybila.parasim.model.ode.ArrayOdeSystemEncoding;
import org.sybila.parasim.model.ode.DefaultOdeSystem;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.suport.gpu.AbstractGpuTest;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


/*
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestOdeSystemWorkspace extends AbstractGpuTest {

    private OdeSystemWorkspace workspace;
    private OdeSystem system;
    
    @BeforeClass
    public void prepareWorkspace() {
        workspace = new OdeSystemWorkspace();
    }
    
    @BeforeClass
    public void prepareSystem() {
        system = new DefaultOdeSystem(new ArrayOdeSystemEncoding(
            new int[] {0, 2, 4},
            new float[] {(float) 10.1, (float) -1, (float) 1, (float) -5.4},
            new int[] {0, 1, 3, 5, 6},
            new int[] {0, 0, 1, 0, 1, 1}            
        ));
    }    
    
    @AfterClass
    public void destroyWorkspace() {
        workspace.free();
    }
    
    @Test
    public void testSaveAndLoad() {
        if (!getContext().isAvailable()) {
            throw new SkipException("The CUDA is not available.");
        }
        workspace.saveSystem(system);
        OdeSystem loaded = workspace.loadSystem();
        float[][] points = new float[][] {
            new float[] {1, 2},
            new float[] {22, 13}
        };
        for (float[] point: points) {
            for (int dim = 0; dim<2; dim++) {
                assertEquals(loaded.value(point, dim), system.value(point, dim), "The value for point " + Arrays.toString(points) + " in dimension <" + dim + "> doesn't match.");
            }
            
        }
    }
    
}
