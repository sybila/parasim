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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLReader;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.xml.XMLException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:jpapouse@fi.muni.cz">Jan Papousek</a>
 */
public class SBMLOdeSystemFactory {

    private static final String SCHEMA_XMLNS = "http://www.w3.org/2001/XMLSchema";

    public static OdeSystem fromFile(File file) throws IOException {
//        validate(file);
        Model model = modelFromFile(file);
        return new SBMLOdeSystem(model);
    }

    protected static Model modelFromFile(File file) throws IOException {
        try {
            return SBMLReader.read(file).getModel();
        } catch(XMLStreamException e) {
            throw new IOException(e);
        }
    }

    protected static void validate(File file) throws IOException {
        /* get schema */
        SchemaFactory schFact = SchemaFactory.newInstance(SCHEMA_XMLNS);
        Schema schema;
        try {
            schema = schFact.newSchema(SBMLOdeSystemFactory.class.getClassLoader().getResource("sbml.xsd"));
        } catch (SAXException saxe) {
            throw new XMLException("Schema could not be lodaded.", saxe);
        }
        Validator valid = schema.newValidator();
        /* get document builder (input parser) */
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder docBuild;
        try {
            docBuild = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException pce) {
            throw new XMLException("Parser could not be configured.", pce);
        }
        /* parsing */
        InputStream input = null;
        try {
            input = new FileInputStream(file);
                        Document doc;
            try {
                doc = docBuild.parse(input);
            } catch (SAXException saxe) {
                throw new XMLException("Parse error during document parsing.",
                        saxe);
            } catch (IOException ioe) {
                throw new XMLException("IO error during document parsing.", ioe);
            }
            /* validation; also enhances result (default attribute values, ...) */
            try {
                valid.validate(new DOMSource(doc));
            } catch (SAXException saxe) {
                throw new XMLException(
                        "Parse error during document validation.", saxe);
            } catch (IOException ioe) {
                throw new XMLException("IO error during document validation.",
                        ioe);
            }
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }

}
