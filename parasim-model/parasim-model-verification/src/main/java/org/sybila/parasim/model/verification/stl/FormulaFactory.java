package org.sybila.parasim.model.verification.stl;

import java.util.Locale;

import org.sybila.parasim.model.xml.XMLFormatException;
import org.sybila.parasim.model.xml.XMLRepresentableFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Factory creating {@link Formula} objects from XML.
 * 
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 *
 */
public class FormulaFactory implements XMLRepresentableFactory<Formula> {

    @Override
    public Formula getObject(Element source) throws XMLFormatException {
        FormulaType type = FormulaType.valueOf(source.getNodeName()
                .toUpperCase(Locale.ENGLISH));
        NodeList children = source.getChildNodes();
        if (type.equals(FormulaType.PREDICATE)) { // predicate
            // FIXME
            return null;
        } else if (type.equals(FormulaType.NOT) || type.equals(FormulaType.AND)
                || type.equals(FormulaType.OR)) { // NOT, OR, AND
            Formula phi = getObject((Element) children.item(0));
            if (type.equals(FormulaType.NOT)) { // NOT
                return new NotFormula(phi);
            } else { // OR, AND
                Formula psi = getObject((Element) children.item(1));
                if (type.equals(FormulaType.AND)) { // AND
                    return new AndFormula(phi, psi);
                } else { // OR
                    return new OrFormula(phi, psi);
                }
            }
        } else if (type.equals(FormulaType.FUTURE)
                || type.equals(FormulaType.GLOBALLY)
                || type.equals(FormulaType.UNTIL)) { // TEMPORAL FORMULAE
            FormulaInterval interval = new FormulaIntervalFactory()
                    .getObject((Element) children.item(0));
            Formula phi = getObject((Element) children.item(1));
            if (type.equals(FormulaType.FUTURE)) {
                return new FutureFormula(phi, interval);
            } else if (type.equals(FormulaType.GLOBALLY)) {
                return new GlobalyFormula(phi, interval);
            } else { // UNTIL
                Formula psi = getObject((Element) children.item(2));
                return new UntilFormula(phi, psi, interval);
            }
        }
        assert false;
        return null;
    }
}
