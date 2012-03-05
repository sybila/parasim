package org.sybila.parasim.model.verification.stl;

import java.net.URL;

import org.sybila.parasim.model.variables.PointVariableMapping;
import org.sybila.parasim.model.xml.FileXMLResource;
import org.sybila.parasim.model.xml.XMLException;
import org.sybila.parasim.model.xml.XMLFormatException;
import org.sybila.parasim.model.xml.XMLRepresentableFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Used to store/load Formula from/to a file.
 * 
 * In order to load predicates, it has to contain mapping between variable names and indices.
 * 
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 *
 */
public class FormulaResource extends FileXMLResource<Formula> {
    private class FormulaContainerFactory implements XMLRepresentableFactory<Formula> {
        @Override
        public Formula getObject(Node source) throws XMLFormatException {
            return new FormulaFactory(mapping).getObject((Element)source.getFirstChild());
        }
    }
    
    private PointVariableMapping mapping;
    
    /**
     * Sets variable name-to-index mapping.
     * @param mapping Mapping between model variable names and indices.
     */
    public void setVariableMapping(PointVariableMapping mapping) {
        this.mapping = mapping;
    }
    
    @Override
    public void load() throws XMLException {
        if (mapping == null) {
            throw new IllegalStateException("Contains no variable mapping to load predicates.");
        }
        super.load();
    }

    @Override
    protected XMLRepresentableFactory<Formula> getFactory() {
        return new FormulaContainerFactory();
    }

    @Override
    protected URL getXMLSchema() {
        return getClass().getClassLoader().getResource("stl_formula.xsd");
    }

    @Override
    protected String getNamespace() {
        return "http://www.sybila.org/parasim/stl-formula";
    }

}
