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

import java.util.List;
import org.sybila.parasim.model.math.Variable;
import dk.ange.octave.OctaveEngine;
import dk.ange.octave.OctaveEngineFactory;
import java.util.ArrayList;
import java.util.Collections;
import org.sybila.parasim.model.math.Constant;
import org.sybila.parasim.model.math.Plus;
import org.sybila.parasim.model.math.Times;
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
        List<Variable> variables = new ArrayList<>();
        final List<OdeSystemVariable> odeSystemVariables = new ArrayList<>();
        for (int i=0; i<2; i++) {
            variables.add(new Variable("y"+i, i));
        }
        odeSystemVariables.add(new OdeSystemVariable(variables.get(0), new Plus(
                new Times(new Constant(10.1f), variables.get(0)),
                new Times(new Constant(-1.0f), variables.get(0), variables.get(1))
        )));
        odeSystemVariables.add(new OdeSystemVariable(variables.get(1), new Plus(
                new Times(new Constant(1.0f), variables.get(0), variables.get(1)),
                new Times(new Constant(-5.4f), variables.get(1))
        )));
        system = new OctaveOdeSystem(new SimpleOdeSystem(odeSystemVariables, Collections.EMPTY_LIST, Collections.EMPTY_LIST));
    }

    @Test
    public void testOctaveStringSimple() {
        String octaveString = system.octaveString(false).trim();
        assertTrue(octaveString.startsWith("function"));
        assertTrue(octaveString.endsWith("endfunction;"));
        assertTrue(octaveString.replace(" ", "").contains("xdot=f(x,t)"));
        System.out.println(octaveString);
        assertTrue(octaveString.replace(" ", "").contains("xdot=zeros(2,1);xdot(1)=10.1*x(1)+(-1.0)*x(1)*x(2);xdot(2)=1.0*x(1)*x(2)+(-5.4)*x(2);"));
    }

    @Test
    public void testOctaveStringWithOctave() {
        OctaveEngine engine = null;
        try {
            engine = new OctaveEngineFactory().getScriptEngine();
            try {
                engine.eval(system.octaveString(false));
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
