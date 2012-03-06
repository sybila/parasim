package org.sybila.parasim.model.verification.stl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.sybila.parasim.model.variables.PointVariableMapping;
import org.sybila.parasim.model.xml.XMLFormatException;
import org.sybila.parasim.model.xml.XMLRepresentableFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Factory creating {@link LinearPredicate} objects from XML.
 * 
 * Needs mapping between variable names and indices.
 * 
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 *
 */
public class LinearPredicateFactory implements
        XMLRepresentableFactory<LinearPredicate> {
    public static final String PREDICATE_NAME = "predicate";
    public static final String VARIABLE_NAME = "variable";
    public static final String MULTIPLIER_ATTRIBUTE = "multiplier";
    public static final String VALUE_NAME = "value";

    private PointVariableMapping mapping;

    /**
     * Creates a new factory with designated variable name-to-index mapping.
     * @param mapping Mapping between variable names and indices.
     */
    public LinearPredicateFactory(PointVariableMapping mapping) {
        if (mapping == null) {
            throw new IllegalArgumentException(
                    "Must receive non-null variable mapping.");
        }
        this.mapping = mapping;
    }

    @Override
    public LinearPredicate getObject(Node source) throws XMLFormatException {
        if (!source.getNodeName().equals(PREDICATE_NAME)) {
            throw new XMLFormatException(
                    "Predicate should have <predicate> node as a root (found `" + source.getNodeName() + "').");
        }
        Float value = null;
        Map<Integer, Float> multipliers = new HashMap<Integer, Float>();
        LinearPredicate.Type type = null;

        NodeList children = source.getChildNodes();
        for (int index = 0; index < children.getLength(); index++) {
            Node child = children.item(index);
            String name = child.getNodeName();

            /* variable and multiplier */
            if (name.equals(VARIABLE_NAME)) {
                String varName = child.getFirstChild().getNodeValue();
                Float multiplier;
                try {
                    multiplier = new Float(child.getAttributes()
                            .getNamedItem(MULTIPLIER_ATTRIBUTE).getNodeValue());
                } catch (NumberFormatException nfe) {
                    throw new XMLFormatException("Illegible number.", nfe);
                }

                Integer var = mapping.getKey(varName);
                if (var == null) {
                    throw new XMLFormatException("Variable `" + varName
                            + "' is not a part of the model.");
                }
                if (multipliers.get(var) != null) {
                    throw new XMLFormatException(
                            "Two occurences of a variable of the same name ("
                                    + varName + ").");
                }
                multipliers.put(var, multiplier);

                /* value */
            } else if (name.equals(VALUE_NAME)) {
                if (value == null) {
                    try {
                        value = new Float(Float.valueOf(child.getFirstChild()
                                .getNodeValue()));
                    } catch (NumberFormatException nfe) {
                        throw new XMLFormatException("Illegible number.", nfe);
                    }
                } else {
                    throw new XMLFormatException(
                            "Mulitple value nodes encountered. Expected only one.");
                }
            } else {

                /* type */
                String typeString = child.getNodeName().toUpperCase(
                        Locale.ENGLISH);
                try {
                    LinearPredicate.Type childType = LinearPredicate.Type
                            .valueOf(typeString);
                    if (type != null) {
                        throw new XMLFormatException(
                                "Two occurences of predicate type.");
                    }
                    type = childType;
                } catch (IllegalArgumentException iae) {
                    throw new XMLFormatException("Unknown element: "
                            + child.getNodeName());
                }
            }
        }

        /* got everything? */
        if (value == null) {
            throw new XMLFormatException(
                    "Predicate has to contain a right-side value.");
        }
        if (multipliers.isEmpty()) {
            throw new XMLFormatException(
                    "Predicate has to contain at least one variable.");
        }
        if (type == null) {
            throw new XMLFormatException(
                    "Predicate has to be of a defined type.");
        }

        return new LinearPredicate(multipliers, value, type, mapping);
    }
}
