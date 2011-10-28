package org.sybila.parasim.model.ode;

import dk.ange.octave.OctaveEngine;
import dk.ange.octave.OctaveEngineFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestOctaveOdeSystem {
    
    private OctaveOdeSystem system;
    
    @BeforeClass
    public void prepareSystem() {
        system = new OctaveOdeSystem(new ArrayOdeSystemEncoding(
            new int[] {0, 2, 4},
            new float[] {(float) 10.1, (float) -1, (float) 1, (float) -5.4},
            new int[] {0, 1, 3, 5, 6},
            new int[] {0, 0, 1, 0, 1, 1}            
        ));
    }
    
    @Test
    public void testOctaveStringSimple() {
        String octaveString = system.octaveString().trim();
        assertTrue(octaveString.startsWith("function"));
        assertTrue(octaveString.endsWith("endfunction"));
        assertTrue(octaveString.replace(" ", "").contains("xdot=f(x,t)"));
        assertTrue(octaveString.replace(" ", "").contains("xdot=zeros(2,1);xdot(0)=10.1*x(0)+(-1.0)*x(0)*x(1);xdot(1)=1.0*x(0)*x(1)+(-5.4)*x(1);"));        
    }
    
    @Test
    public void testOctaveStringWithOctave() {
        OctaveEngine engine = null;
        try {
            engine = new OctaveEngineFactory().getScriptEngine();
            try {
                engine.eval(system.octaveString());
            }
            catch(Exception e) {
                e.printStackTrace(System.err);
                fail("The octave string can not be used with octave engine.");
            }
        }
        catch(Exception ignored) {}        
        finally {
            if (engine != null) {
                engine.close();
            }
        }        
    }
    
}
