package org.sybila.parasim.model.verification.stl;

import java.net.URL;

import org.sybila.parasim.model.xml.FileXMLResource;
import org.sybila.parasim.model.xml.XMLFormatException;
import org.sybila.parasim.model.xml.XMLRepresentableFactory;
import org.w3c.dom.Element;

/**
 * Used to store/load Formula from/to a file.
 * 
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 *
 */
public class FormulaResource extends FileXMLResource<Formula> {
    private class FormulaContainerFactory implements XMLRepresentableFactory<Formula> {
        @Override
        public Formula getObject(Element source) throws XMLFormatException {
            return new FormulaFactory().getObject((Element)source.getFirstChild());
        }
    }

    @Override
    protected XMLRepresentableFactory<Formula> getFactory() {
        return new FormulaContainerFactory();
    }

    @Override
    protected URL getXMLSchema() {
        return getClass().getResource("stl_formula.xsd");
    }

    @Override
    protected String getNamespace() {
        return "http://www.sybila.org/parasim/stl-formula";
    }

}
