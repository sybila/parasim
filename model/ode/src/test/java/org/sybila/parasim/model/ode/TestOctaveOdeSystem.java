/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.model.ode;

import dk.ange.octave.OctaveEngine;
import dk.ange.octave.OctaveEngineFactory;
import org.testng.SkipException;
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
        assertTrue(octaveString.replace(" ", "").contains("xdot=zeros(2,1);xdot(1)=10.1*x(1)+(-1.0)*x(1)*x(2);xdot(2)=1.0*x(1)*x(2)+(-5.4)*x(2);"));        
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
        catch(Exception ignored) {
            throw new SkipException("The Octave is not available.");
        }        
        finally {
            if (engine != null) {
                engine.close();
            }
        }        
    }
    
}
