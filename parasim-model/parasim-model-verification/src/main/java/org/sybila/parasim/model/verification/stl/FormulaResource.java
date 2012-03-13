package org.sybila.parasim.model.verification.stl;

import java.io.File;
import java.net.URL;

import org.sybila.parasim.model.variables.PointVariableMapping;
import org.sybila.parasim.model.xml.FileXMLResource;
import org.sybila.parasim.model.xml.XMLException;
import org.sybila.parasim.model.xml.XMLFormatException;
import org.sybila.parasim.model.xml.XMLRepresentableFactory;
import org.w3c.dom.Document;
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
    private static final String FORMULA_NAME = "formula";
    private static class FormulaContainer implements Formula {
        public Formula rootFormula;
        
        @Override
        public Element toXML(Document doc) {
            Element form = doc.createElement(FORMULA_NAME);
            form.appendChild(rootFormula.toXML(doc));
            return form;
        }
        
        @Override
        public int getArity() {
            throw new UnsupportedOperationException("Mockup container.");
        }
        
        @Override
        public Formula getSubformula(int index) {
            throw new UnsupportedOperationException("Mockup container.");
        }
        
        @Override
        public float getTimeNeeded() {
            throw new UnsupportedOperationException("Mockup container.");
        }
        
        @Override
        public FormulaType getType() {
            throw new UnsupportedOperationException("Mockup container.");
        }
    }
    private class FormulaContainerFactory implements XMLRepresentableFactory<Formula> {
        @Override
        public Formula getObject(Node source) throws XMLFormatException {
            return new FormulaFactory(mapping).getObject(source.getFirstChild());
        }
    }
    
    private PointVariableMapping mapping;
    private FormulaContainer container;
    
    /**
     * Initiates file to store/load element (see {@link FileXMLResource#FileXMLResource(File)}).
     */
    public FormulaResource(File file) {
        super(file);
        container = new FormulaContainer();
        super.setRoot(container);
    }
    
    @Override
    public void setRoot(Formula target) {
        container.rootFormula = target;
    }
    
    @Override
    public Formula getRoot() {
        return container.rootFormula;
    }
    
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
