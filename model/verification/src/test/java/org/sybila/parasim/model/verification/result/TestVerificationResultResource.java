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
package org.sybila.parasim.model.verification.result;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.SchemaFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class TestVerificationResultResource {

    private VerificationResultResource resource;

    @BeforeMethod
    public void initResource() throws IOException {
        File tmpFile = File.createTempFile("parasim", "resource");
        tmpFile.deleteOnExit();
        resource = new VerificationResultResource(tmpFile);
    }

    /**
     * Tests whether resource loads valid XML schema.
     */
    @Test
    public void testSchema() {
        URL schema = resource.getXMLSchema();
        if (schema == null) {
            fail("No URL returned.");
        }

        SchemaFactory schemFact = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        try {
            schemFact.newSchema(schema);
        } catch (SAXException saxe) {
            saxe.printStackTrace();
            fail("Schema could not be loaded.");
        }
    }

    @Test
    public void testNamespace() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder docBuild = null;
        try {
            docBuild = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
            fail("Could not configure parser.");
        }

        Document doc = null;
        try {
            doc = docBuild.parse(new File(resource.getXMLSchema().toURI()));
        } catch (SAXException saxe) {
            saxe.printStackTrace();
            fail("Could not parse the schema.");
        } catch (IOException ioe) {
            ioe.printStackTrace();
            fail("Could not load schema.");
        } catch (URISyntaxException urise) {
            urise.printStackTrace();
            fail("Wrong schema URL syntax.");
        }
        String namespace = doc.getDocumentElement().getAttribute("targetNamespace");
        assertEquals(namespace, resource.getNamespace(), "Target namespace of schema given by getXMLSchema should be the same as namespace given by getNamespace");
    }
}
