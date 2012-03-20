package org.sybila.parasim.model.space;

import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.xml.XMLFormatException;
import org.sybila.parasim.model.xml.XMLRepresentableFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class OrthogonalSpaceFactory implements
        XMLRepresentableFactory<OrthogonalSpace> {
    public static final String SPACE_NAME = "space";
    public static final String DIMENSION_NAME = "dimension";
    public static final String ATTRIBUTE_MIN = "min";
    public static final String ATTRIBUTE_MAX = "max";

    @Override
    public OrthogonalSpace getObject(Node source) throws XMLFormatException {
        if (!source.getNodeName().equals(SPACE_NAME)) {
            throw new XMLFormatException(
                    "Wrong name of space element (expected `" + SPACE_NAME
                            + "', received `" + source.getNodeName() + "').");
        }
        NodeList children = source.getChildNodes();
        int dimension = children.getLength();
        float[] min = new float[dimension];
        float[] max = new float[dimension];
        for (int index = 0; index < dimension; index++) {
            Node child = children.item(index);
            if (child.getNodeName().equals(DIMENSION_NAME)) {
                NamedNodeMap attr = child.getAttributes();
                try {
                    min[index] = Float.valueOf(attr.getNamedItem(ATTRIBUTE_MIN)
                            .getNodeValue());
                    max[index] = Float.valueOf(attr.getNamedItem(ATTRIBUTE_MAX)
                            .getNodeValue());
                } catch (NumberFormatException nfe) {
                    throw new XMLFormatException("Illegible number.", nfe);
                }
                if (min[index] > max[index]) {
                    throw new XMLFormatException("The min bound in dimension <"
                            + index + "> is greater than max bound ("
                            + min[index] + ">" + max[index] + ").");
                }
            } else {
                throw new XMLFormatException("Unknown element: "
                        + child.getNodeName());
            }
        }
        Point minBounds = new ArrayPoint(0, min, 0, dimension);
        Point maxBounds = new ArrayPoint(0, max, 0, dimension);
        return new OrthogonalSpace(minBounds, maxBounds);
    }

}
