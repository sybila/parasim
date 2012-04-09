package org.sybila.parasim.model.verification.stl;

import org.sybila.parasim.model.xml.XMLFormatException;
import org.sybila.parasim.model.xml.XMLRepresentableFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Factory creating {@link TimeInterval} objects from XML. Also contains some
 * auxiliary methods for converting {@link FormulaInterval} to XML.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 *
 */
public class FormulaIntervalFactory implements
        XMLRepresentableFactory<FormulaInterval> {

    /** Name of XML element representing FormulaInterval */
    public static final String INTERVAL_NAME = "interval";
    /** Name of element representing lower bound */
    private static final String LOWER_NAME = "lower";
    /** Name of element representing upper bound */
    private static final String UPPER_NAME = "upper";
    /** Name of attribute representing type of boundary */
    private static final String TYPE_NAME = "type";
    /**
     * Value of type boundary: open -- corresponds to
     * {@link IntervalBoundaryType#OPEN}
     */
    private static final String OPEN_NAME = "open";
    /**
     * Value of type boundary: closed -- corresponds to
     * {@link IntervalBoundaryType#CLOSED}
     */
    private static final String CLOSED_NAME = "closed";

    /**
     * @return Element corresponding to one bound.
     * @param doc
     *            Parent document
     * @param name
     *            LOWER_NAME or UPPER_NAME
     */
    private static Element getBound(Document doc, String name, float value,
            IntervalBoundaryType type) {
        Element target = doc.createElement(name);
        target.appendChild(doc.createTextNode(Float.toString(value)));
        String typeName;
        if (type.equals(IntervalBoundaryType.CLOSED)) {
            typeName = CLOSED_NAME;
        } else {
            assert type.equals(IntervalBoundaryType.OPEN) : type;
            typeName = OPEN_NAME;
        }
        target.setAttribute(TYPE_NAME, typeName);
        return target;
    }

    /**
     * Used by {@link XMLRepresentable#toXML} method to obtain XML element
     * representing interval upper bound.
     *
     * @param doc
     *            Parent XML document.
     * @param value
     *            Bound value.
     * @param type
     *            Bound type.
     * @return XML element representing upper bound.
     */
    public static Element getUpperBound(Document doc, float value,
            IntervalBoundaryType type) {
        return getBound(doc, UPPER_NAME, value, type);
    }

    /**
     * Used by {@link XMLRepresentable#toXML} method to obtain XML element
     * representing interval lower bound.
     *
     * @param doc
     *            Parent XML document.
     * @param value
     *            Bound value.
     * @param type
     *            Bound type
     * @return XML element representing lower bound.
     */
    public static Element getLowerBound(Document doc, float value,
            IntervalBoundaryType type) {
        return getBound(doc, LOWER_NAME, value, type);
    }

    /**
     * @return Type of <code>parent</code>'s boundary.
     */
    private static IntervalBoundaryType getBoundaryType(Node parent)
            throws XMLFormatException {
        String target = parent.getAttributes().getNamedItem(TYPE_NAME).getNodeValue();
        if (target.equals(OPEN_NAME)) {
            return IntervalBoundaryType.OPEN;
        } else if (target.equals(CLOSED_NAME)) {
            return IntervalBoundaryType.CLOSED;
        } else {
            throw new XMLFormatException("Unknown boundary type: " + target
                    + " (expected \"" + OPEN_NAME + "\" or \"" + CLOSED_NAME
                    + "\").");
        }
    }

    @Override
    public FormulaInterval getObject(Node source) throws XMLFormatException {
        NodeList bounds = source.getChildNodes();
        float upper = 0;
        float lower = 0;
        IntervalBoundaryType upperType = null;
        IntervalBoundaryType lowerType = null;

        for (int index = 0; index < bounds.getLength(); index++) {
            Node bound = bounds.item(index);
            /* LOWER BOUND */
            if (bound.getNodeName().equals(LOWER_NAME)) {
                try {
                    lower = Float.valueOf(bound.getFirstChild().getNodeValue());
                } catch (NumberFormatException nfe) {
                    throw new XMLFormatException("Illegible nubmer.", nfe);
                }
                lowerType = getBoundaryType(bound);

                /* UPPER BOUND */
            } else if (bound.getNodeName().equals(UPPER_NAME)) {
                try {
                    upper = Float.valueOf(bound.getFirstChild().getNodeValue());
                } catch (NumberFormatException nfe) {
                    throw new XMLFormatException("Illegible number.", nfe);
                }
                upperType = getBoundaryType(bound);

                /* UNKNOWN BOUND */
            } else {
                throw new XMLFormatException("Unknown boundary: "
                        + bound.getNodeName() + " (expected \"" + UPPER_NAME
                        + "\" or \"" + LOWER_NAME + "\").");
            }
        }

        /* CONTROL */
        if (upperType == null) {
            throw new XMLFormatException("Upper boundary not set.");
        }
        if (lowerType == null) {
            throw new XMLFormatException("Lower boundary not set.");
        }
        if (lower <= 0) {
            throw new XMLFormatException(
                    "Lower bound of an interval must be greater or equal to zero.");
        }
        if (upper < lower) {
            throw new XMLFormatException(
                    "Upper bound of an interval must be greater (or equal) to lower.");
        }
        return new TimeInterval(lower, upper, lowerType, upperType);
    }
}
