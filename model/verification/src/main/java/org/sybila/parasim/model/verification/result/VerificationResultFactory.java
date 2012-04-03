package org.sybila.parasim.model.verification.result;

import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.xml.XMLFormatException;
import org.sybila.parasim.model.xml.XMLRepresentableFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class VerificationResultFactory implements XMLRepresentableFactory<VerificationResult> {

    public static final String RESULT_NAME = "result";
    public static final String POINT_NAME = "point";
    public static final String DIMENSION_NAME = "dim";
    public static final String ROBUSTNESS_NAME = "robustness";

    public VerificationResult getObject(Node source) throws XMLFormatException {
        NodeList children = source.getChildNodes();

        int size = children.getLength();
        if (size == 0) {
            return new ArrayVerificationResult(0, new Point[0], new float[0]);
        }

        Point[] points = new Point[size];
        float[] robustness = new float[size];
        int dimension = children.item(0).getChildNodes().getLength() - 1; //one of attributes is robustness

        for (int i = 0; i < size; i++) {
            NodeList dimNodes = children.item(i).getChildNodes();
            if (dimNodes.getLength() != dimension + 1) { //Necessary
                throw new XMLFormatException("Unexpected number of dimensions.");
            }
            try {
                float[] dims = new float[dimension];
                for (int j = 0; j < dimension; j++) {
                    dims[j] = Float.valueOf(dimNodes.item(j).getFirstChild().getNodeValue());
                }
                robustness[i] = Float.valueOf(dimNodes.item(dimension).getFirstChild().getNodeValue());
                points[i] = new ArrayPoint(0, dims, 0, dimension);
            } catch (NumberFormatException nfe) {
                throw new XMLFormatException("Illegible number.", nfe);
            }
        }

        return new ArrayVerificationResult(size, points, robustness);
    }
}
