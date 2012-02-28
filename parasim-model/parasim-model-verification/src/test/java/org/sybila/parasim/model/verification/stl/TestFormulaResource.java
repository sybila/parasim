package org.sybila.parasim.model.verification.stl;

import static org.testng.Assert.fail;

import java.net.URL;

import javax.xml.validation.SchemaFactory;

import org.testng.annotations.*;
import org.xml.sax.SAXException;

public class TestFormulaResource {
    private FormulaResource resource;
    
    @BeforeMethod
    public void createResource() {
        resource = new FormulaResource();
    }

    /**
     * Tests whether the resource loads a valid XML schema.
     */
    @Test
    public void testGetSchema() {
        URL schema = resource.getXMLSchema();
        if (schema == null) {
            fail("No URL returned.");
        }
        
        SchemaFactory schemFact = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        try {
            schemFact.newSchema(schema);
        } catch (SAXException saxe) {
            saxe.printStackTrace();
            fail("Schema could not be loaded");
        }
    }
    
    @Test
    public void testNamespace() {
        
    }
    

}
