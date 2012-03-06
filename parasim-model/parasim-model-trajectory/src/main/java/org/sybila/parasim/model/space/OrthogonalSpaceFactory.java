package org.sybila.parasim.model.space;

import org.sybila.parasim.model.xml.XMLFormatException;
import org.sybila.parasim.model.xml.XMLRepresentableFactory;
import org.w3c.dom.Node;

public class OrthogonalSpaceFactory implements
        XMLRepresentableFactory<OrthogonalSpace> {
    public static final String SPACE_NAME = "space";
    public static final String DIMENSION_NAME = "dimension";
    public static final String ATTRIBUTE_MIN = "min";
    public static final String ATTRIBUTE_MAX = "max";

    @Override
    public OrthogonalSpace getObject(Node source) throws XMLFormatException {
        // TODO Auto-generated method stub
        return null;
    }

}
