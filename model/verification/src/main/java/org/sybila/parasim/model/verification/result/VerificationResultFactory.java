/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.model.verification.result;

import java.util.ArrayList;
import java.util.List;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.PointWithNeigborhoodWrapper;
import org.sybila.parasim.model.trajectory.PointWithNeighborhood;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.model.verification.SimpleRobustness;
import org.sybila.parasim.model.xml.FloatFactory;
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
    public static final String DIMENSION_NAME = "dimension";
    public static final String DATA_NAME = "data";
    public static final String ROBUSTNESS_NAME = "robustness";

    @Override
    public VerificationResult getObject(Node source) throws XMLFormatException {
        NodeList children = source.getChildNodes();

        int size = children.getLength();
        if (size == 0) {
            return new ArrayVerificationResult(new PointWithNeighborhood[0], new Robustness[0]);
        }

        List<Robustness> loadedRobustnesses = new ArrayList<>();
        List<PointWithNeighborhood> loadedPoints = new ArrayList<>();
        int dimension = Integer.parseInt(source.getAttributes().getNamedItem(DIMENSION_NAME).getNodeValue());

        for (int i = 0; i < size; i++) {
            Node pointNode = children.item(i);
            float[] dims = null;
            for (int j = 0; j < pointNode.getChildNodes().getLength(); j++) {
                switch (pointNode.getChildNodes().item(j).getNodeName()) {
                    case DATA_NAME:
                        Node dataNode = pointNode.getChildNodes().item(j);
                        if (dataNode.getChildNodes().getLength() != dimension) {
                            throw new XMLFormatException("Unexpected number of dimensions.");
                        }
                        dims = new float[dimension];
                        for (int d = 0; d < dataNode.getChildNodes().getLength(); d++) {
                            dims[d] = FloatFactory.getObject(dataNode.getChildNodes().item(d).getFirstChild());
                        }
                        break;
                }
                loadedRobustnesses.add(new SimpleRobustness(FloatFactory.getObject(pointNode.getAttributes().getNamedItem(ROBUSTNESS_NAME))));
                loadedPoints.add(new PointWithNeigborhoodWrapper(new ArrayPoint(0, dims)));
            }
        }

        PointWithNeighborhood[] points = new PointWithNeighborhood[loadedPoints.size()];
        Robustness[] robustnesses = new Robustness[loadedRobustnesses.size()];
        points = loadedPoints.toArray(points);
        robustnesses = loadedRobustnesses.toArray(robustnesses);

        return new ArrayVerificationResult(points, robustnesses, new SimpleRobustness(FloatFactory.getObject(source.getAttributes().getNamedItem(ROBUSTNESS_NAME))));
    }
}
