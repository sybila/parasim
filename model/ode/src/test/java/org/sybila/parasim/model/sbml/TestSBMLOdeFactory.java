/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.model.sbml;

import java.net.URISyntaxException;
import org.sybila.parasim.model.trajectory.Point;
import java.util.Set;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:jpapouse@fi.muni.cz">Jan Papousek</a>
 */
public class TestSBMLOdeFactory {

    private OdeSystem system;

    @BeforeMethod
    public void setUp() throws IOException, URISyntaxException {
        system = SBMLOdeSystemFactory.fromFile(new File(getClass().getClassLoader().getResource("org/sybila/parasim/model/sbml/biomodel.xml").toURI()));
    }

    @Test
    public void testDimension() throws IOException {
        assertEquals(system.dimension(), 5);
    }

    @Test
    public void testVariableNames() {
        Set<String> names = new HashSet<>(Arrays.asList("A", "B", "C", "D", "CD"));
        Set<String> foundNames = new HashSet<>();
        for (int dim=0; dim<system.dimension(); dim++) {
            foundNames.add(system.getVariable(dim).getName());
        }
        assertEquals(foundNames, names);
    }

    @Test
    public void testValue() {
        Point point = new ArrayPoint(0, (float) 0.1, (float) 0.2, (float) 0.3, (float) 0.4, (float) 0.5);
        float[] expected = new float[] {(float) (- 0.211111365), (float) (0.211111365), (float) (- 0.48444546), (float) (- 0.48444546), (float) (0.48444546)};
        for (int dim=0; dim<point.getDimension(); dim++) {
            assertEquals(system.getVariable(dim).getRightSideExpression().evaluate(point), expected[dim], 0.01, "The value in dimension <" + dim + "> doesn't match.");
        }
    }

}
