package org.sybila.parasim.model.verification.stl;

import static org.sybila.parasim.model.verification.stl.IntervalBoundaryType.CLOSED;
import static org.sybila.parasim.model.verification.stl.IntervalBoundaryType.OPEN;
import static org.testng.Assert.fail;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.sybila.parasim.model.variables.PointVariableMapping;
import org.sybila.parasim.model.verification.stl.LinearPredicate.Type;
import org.sybila.parasim.model.xml.XMLException;
import org.testng.annotations.*;

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
            } else
                return null;
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
    
    private static Formula getTestFormula() {
        Map<Integer, Float> andOrFPterms = new HashMap<Integer, Float>();
        andOrFPterms.put(new Integer(0), new Float(3.87));
        andOrFPterms.put(new Integer(1), new Float(-2.54));
        Predicate andOrFP = new LinearPredicate(andOrFPterms, 15.28f, Type.EQUALS, new SimpleMapping());
        
        Map<Integer, Float> andOrGPterms = new HashMap<Integer, Float>();
        andOrGPterms.put(new Integer(1), new Float(-2.159));
        andOrGPterms.put(new Integer(2), new Float(0.157));
        Predicate andOrGP = new LinearPredicate(andOrGPterms, 0.29f, Type.GREATER, new SimpleMapping());
        
        Map<Integer, Float> andNotUP1terms = new HashMap<Integer, Float>();
        andNotUP1terms.put(new Integer(0), new Float(-1.004));
        andNotUP1terms.put(new Integer(2), new Float(0.081));
        Predicate andNotUP1 = new LinearPredicate(andNotUP1terms, 0.23f, Type.LESSER, new SimpleMapping());
        
        Map<Integer, Float> andNotUP2terms = new HashMap<Integer, Float>();
        andNotUP2terms.put(new Integer(2), new Float(185.123));
        Predicate andNotUP2 = new LinearPredicate(andNotUP2terms, 191.25f, Type.EQUALS, new SimpleMapping());

        FormulaInterval andOrFI = new TimeInterval(7.8f, 10f, CLOSED, OPEN);
        Formula andOrF = new FutureFormula(andOrFP, andOrFI);
        FormulaInterval andOrGI = new TimeInterval(0.001f, 1.002f, OPEN, OPEN);
        Formula andOrG = new GlobalyFormula(andOrGP, andOrGI);
        Formula andOr = new OrFormula(andOrF, andOrG);

        FormulaInterval andNotUI = new TimeInterval(129.34f, 301.22f, CLOSED,
                CLOSED);
        Formula andNotU = new UntilFormula(andNotUP1, andNotUP2, andNotUI);
        Formula andNot = new NotFormula(andNotU);

        Formula and = new AndFormula(andOr, andNot);
        return and;
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
        resource = new FormulaResource();
        resource.setVariableMapping(new SimpleMapping());
    }

    @Test
    public void tryStore() {
         
    }
    
    @Test
    public void tryLoad() {
        resource.setTargetFile(getTestFormulaFile());
        try {
            resource.load();
        } catch (XMLException xmle) {
            xmle.printStackTrace();
            fail("XML Error: " + xmle.getMessage());
        }
    }
    
}