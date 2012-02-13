package org.sybila.parasim.model.sbml;

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
    public void setUp() throws IOException {
        system = SBMLOdeSystemFactory.fromFile(new File("src/test/xml/biomodel.xml"));
    }
    
    @Test
    public void testDimension() throws IOException {
        assertEquals(system.dimension(), 5);
    }
    
    @Test
    public void testVariableNames() {
        Set<String> names = new HashSet<String>(Arrays.asList("A", "B", "C", "D", "CD"));
        Set<String> foundNames = new HashSet<String>();
        for (int dim=0; dim<system.dimension(); dim++) {
            foundNames.add(system.getVariable(dim).getName());
        }
        assertEquals(foundNames, names);
    }
    
    @Test
    public void testValue() {
        Point point = new ArrayPoint(0, (float) 0.1, (float) 0.2, (float) 0.3, (float) 0.4, (float) 0.5);
        float[] expected = new float[] {(float) -0.211111381649971, (float) 0.211111381649971, (float) 1.5340772867202759, (float) 1.5340772867202759, (float) - 1.5340772867202759};
        for (int dim=0; dim<point.getDimension(); dim++) {
            assertEquals(system.value(point, dim), expected[dim], 0.0000001, "The value in dimension <" + dim + "> doesn't match.");
        }
    }
    
}
