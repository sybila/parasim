package org.sybila.parasim.model.ode;

import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.Point;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestDefaultOdeSystem {
    
    OdeSystem system;
    
    @BeforeMethod
    public void setUp() {
        system = new DefaultOdeSystem(
            new ArrayOdeSystemEncoding(
                new int[] {0, 2, 3},
                new float[] {(float)0.1, (float) 0.2, (float) 0.3},
                new int[] {0, 1, 3, 3},
                new int[] {0, 1, 1}
            )
        );
    }
    
    @Test
    public void testValue() {
        Point point = new ArrayPoint(0, 5, 10);
        assertEquals(system.value(point, 0), (float) 20.5);
        assertEquals(system.value(point, 1), (float) 0.3);
    }
    
}
