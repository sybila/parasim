/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.model.verification.stl;

import static org.sybila.parasim.model.verification.stl.IntervalBoundaryType.CLOSED;
import static org.sybila.parasim.model.verification.stl.IntervalBoundaryType.OPEN;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.sybila.parasim.model.ode.PointVariableMapping;
import org.sybila.parasim.model.verification.stl.LinearPredicate.Type;
import org.sybila.parasim.model.xml.XMLException;
import org.sybila.parasim.util.Pair;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestFormulaStorage {

    public static class SimpleMapping implements PointVariableMapping {

        @Override
        public Integer getKey(String variableName) {
            if (variableName.equals("x")) {
                return 0;
            } else if (variableName.equals("y")) {
                return 1;
            } else if (variableName.equals("z")) {
                return 2;
            } else {
                return null;
            }
        }

        @Override
        public String getName(Integer variableKey) {
            switch (variableKey) {
                case 0:
                    return "x";
                case 1:
                    return "y";
                case 2:
                    return "z";
                default:
                    return null;
            }
        }

        @Override
        public int getDimension() {
            return 3;
        }

        @Override
        public boolean equals(Object obj) {
            return (obj instanceof SimpleMapping);
        }

        @Override
        public int hashCode() {
            return 5;
        }
    }
    private FormulaResource resource;

    private static void putValue(Map<Pair<Integer, Integer>, Float> map, int variable, int star, float value) {
        map.put(new Pair<>(new Integer(variable), new Integer(star)), new Float(value));
    }

    private static Formula getTestFormula() {
        Map<Pair<Integer, Integer>, Float> andOrFPterms = new HashMap<>();
        putValue(andOrFPterms, 0, 0, 3.78f);
        putValue(andOrFPterms, 1, 1, -2.54f);
        Predicate andOrFP = new LinearPredicate(andOrFPterms, 15.28f,
                Type.EQUALS, new SimpleMapping());

        Map<Pair<Integer, Integer>, Float> andOrGPterms = new HashMap<>();
        putValue(andOrGPterms, 1, 2, -2.159f);
        putValue(andOrGPterms, 2, 0, 0.157f);
        Predicate andOrGP = new LinearPredicate(andOrGPterms, 0.29f,
                Type.GREATER, new SimpleMapping());

        Map<Pair<Integer, Integer>, Float> andNotUP1terms = new HashMap<>();
        putValue(andNotUP1terms, 0, 0, -1.004f);
        putValue(andNotUP1terms, 2, 1, 0.081f);
        Predicate andNotUP1 = new LinearPredicate(andNotUP1terms, 0.23f,
                Type.LESSER, new SimpleMapping());

        Map<Pair<Integer, Integer>, Float> andNotUP2terms = new HashMap<>();
        putValue(andNotUP2terms, 2, 0, 185.123f);
        Predicate andNotUP2 = new LinearPredicate(andNotUP2terms, 191.25f,
                Type.EQUALS, new SimpleMapping());

        FormulaInterval andOrFI = new TimeInterval(7.8f, 10f, CLOSED, OPEN);
        Formula andOrF = new FreezeFormula(new FutureFormula(andOrFP, andOrFI), 2);
        FormulaInterval andOrGI = new TimeInterval(0.001f, 1.002f, OPEN, OPEN);
        Formula andOrG = new GloballyFormula(andOrGP, andOrGI);
        Formula andOr = new OrFormula(andOrF, andOrG);

        FormulaInterval andNotUI = new TimeInterval(129.34f, 301.22f, CLOSED,
                CLOSED);
        Formula andNotU = new FreezeFormula(new UntilFormula(andNotUP1, andNotUP2, andNotUI), 3);
        Formula andNot = new NotFormula(andNotU);

        Formula and = new AndFormula(andOr, andNot);
        return new FreezeFormula(and, 1);
    }

    private File getTestFormulaFile() {
        URL res = getClass().getClassLoader().getResource("testFormula.xml");
        try {
            return new File(res.toURI());
        } catch (URISyntaxException urie) {
            urie.printStackTrace();
            fail("Could get to test formula file.");
        }
        return null;
    }

    @BeforeMethod
    public void prepareFormulaResource() {
    }

    @Test
    public void tryLoad() {
        // Tady jsme se dostali do zásadních problémů -- XML parser z nějakého
        // důvodu dohazuje prázdné textové elementy!
        resource = new FormulaResource(getTestFormulaFile());
        resource.setVariableMapping(new SimpleMapping());
        try {
            resource.load();
        } catch (XMLException xmle) {
            if (xmle.getCause() != null) {
                xmle.getCause().printStackTrace();
            }
            fail("XML Error: " + xmle.getMessage());
        }
    }

    @Test(enabled = false)
    public void tryStore() {
        File temp = null;
        try {
            temp = File.createTempFile("formula", ".xml");
        } catch (IOException ioe) {
            ioe.printStackTrace();
            fail("Could not create temporary file.");
        }
        temp.deleteOnExit();

        resource = new FormulaResource(temp);
        resource.setVariableMapping(new SimpleMapping());
        resource.setRoot(getTestFormula());
        try {
            resource.store();
        } catch (XMLException xmle) {
            if (xmle.getCause() != null) {
                xmle.getCause().printStackTrace();
            }
            fail("Could not print XML document: " + xmle.getMessage());
        }

        BufferedReader result = null;
        try {
            result = new BufferedReader(new InputStreamReader(
                    new FileInputStream(temp)));
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            fail("Result file (just written into) was lost.");
        }
        BufferedReader reference = null;
        try {
            reference = new BufferedReader(new InputStreamReader(
                    new FileInputStream(getTestFormulaFile())));
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            fail("Reference file could not be found.");
        }

        try {
            String resLine = result.readLine();
            String refLine = reference.readLine();
            while ((resLine != null) && (refLine != null)) {
                assertEquals(resLine, refLine,
                        "Resulting file should be different.");
                refLine = reference.readLine();
                resLine = result.readLine();
            }
            if ((resLine != null) || (refLine != null)) {
                fail("Resulting and reference files have different lengths.");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            fail("IO error during file comparison.");
        } finally {
            try {
                result.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
                fail("Could not close result file.");
            }
            try {
                reference.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
                fail("Could not close reference file.");
            }
        }


    }
}
