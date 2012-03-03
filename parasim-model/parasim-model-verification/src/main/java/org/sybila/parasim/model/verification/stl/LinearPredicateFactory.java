package org.sybila.parasim.model.verification.stl;

import org.sybila.parasim.model.xml.XMLFormatException;
import org.sybila.parasim.model.xml.XMLRepresentableFactory;
import org.w3c.dom.Node;

public class LinearPredicateFactory implements
        XMLRepresentableFactory<LinearPredicate> {
    public static final String PREDICATE_NAME = "predicate";
    public static final String VARIABLE_NAME = "variable";
    public static final String MULTIPLIER_ATTRIBUTE = "multiplier";
    public static final String VALUE_NAME = "value";

    @Override
    public LinearPredicate getObject(Node source) throws XMLFormatException {
        // TODO Auto-generated method stub
        return null;
    }

}
