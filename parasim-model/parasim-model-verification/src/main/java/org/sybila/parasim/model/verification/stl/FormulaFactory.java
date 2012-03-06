package org.sybila.parasim.model.verification.stl;

import java.util.Locale;

import org.sybila.parasim.model.variables.PointVariableMapping;
import org.sybila.parasim.model.xml.XMLFormatException;
import org.sybila.parasim.model.xml.XMLRepresentableFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Factory creating {@link Formula} objects from XML.
 * 
 * In order to load predicates, it contains mapping between variable names and indices.
 * 
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 * 
 */
public class FormulaFactory implements XMLRepresentableFactory<Formula> {
    private PointVariableMapping mapping;

    /**
     * Sets contained variable name-to-index mapping.
     * @param mapping Mapping between model variable names and indices. 
     */
    public FormulaFactory(PointVariableMapping mapping) {
        this.mapping = mapping;
    }
    
    @Override
    public Formula getObject(Node source) throws XMLFormatException {
        FormulaType type;
        try {
            type = FormulaType.valueOf(source.getNodeName().toUpperCase(
                    Locale.ENGLISH));
        } catch (IllegalArgumentException iae) {
            throw new XMLFormatException(
                    "Invalid document: unknown name of element: "
                            + source.getNodeName());
        }
        NodeList children = source.getChildNodes();

        /* PREDICATE */
        if (type.equals(FormulaType.PREDICATE)) {
            return new LinearPredicateFactory(mapping).getObject(source);

            /* NOT, OR, AND */
        } else if (type.equals(FormulaType.NOT) || type.equals(FormulaType.AND)
                || type.equals(FormulaType.OR)) {
            if (children.getLength() < 1) {
                throw new XMLFormatException(
                        "Too few subformulae: 0 (expected at least 1).");
            }
            Formula phi = getObject(children.item(0));

            /* NOT */
            if (type.equals(FormulaType.NOT)) {
                return new NotFormula(phi);

                /* OR, AND */
            } else {
                if (children.getLength() < 2) {
                    throw new XMLFormatException(
                            "Too few subformulae: 1 (expected 2).");
                }
                Formula psi = getObject(children.item(1));

                /* AND */
                if (type.equals(FormulaType.AND)) {
                    return new AndFormula(phi, psi);
                    /* OR */
                } else {
                    return new OrFormula(phi, psi);
                }
            }

            /* TEMPORAL FORMULAE */
        } else if (type.equals(FormulaType.FUTURE)
                || type.equals(FormulaType.GLOBALLY)
                || type.equals(FormulaType.UNTIL)) {

            if (children.getLength() < 2) {
                throw new XMLFormatException("Too few children: "
                        + children.getLength() + " (expected at least 2).");
            }

            FormulaInterval interval = new FormulaIntervalFactory()
                    .getObject(children.item(0));
            Formula phi = getObject(children.item(1));

            /* FUTURE */
            if (type.equals(FormulaType.FUTURE)) {
                return new FutureFormula(phi, interval);

                /* GLOBALLY */
            } else if (type.equals(FormulaType.GLOBALLY)) {
                return new GlobalyFormula(phi, interval);

                /* UNTIL */
            } else {
                if (children.getLength() < 3) {
                    throw new XMLFormatException("Too few children: 2 (expected 3).");
                }
                Formula psi = getObject(children.item(2));
                return new UntilFormula(phi, psi, interval);
            }
        }
        assert false;
        return null;
    }
}
