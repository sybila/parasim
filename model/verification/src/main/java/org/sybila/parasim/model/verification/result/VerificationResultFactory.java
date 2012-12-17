/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.Point;
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
    public static final String ID_NAME = "id";
    public static final String DIMENSION_NAME = "dimension";
    public static final String DATA_NAME = "data";
    public static final String ROBUSTNESS_NAME = "robustness";
    public static final String NEIGHBORS_NAME = "neighbors";

    @Override
    public VerificationResult getObject(Node source) throws XMLFormatException {
        NodeList children = source.getChildNodes();

        int size = children.getLength();
        if (size == 0) {
            return new ArrayVerificationResult(new PointWithNeighborhood[0], new Robustness[0]);
        }

        Map<Integer, Robustness> robustnesses = new HashMap<>();
        Map<Integer, Point> memory = new HashMap<>();
        Map<Integer, Collection<Integer>> neighbors = new HashMap<>();
        int dimension = Integer.parseInt(source.getAttributes().getNamedItem(DIMENSION_NAME).getNodeValue());

        for (int i = 0; i < size; i++) {
            Node pointNode = children.item(i);
            float[] dims = null;
            Collection<Integer> currentNeighbors = null;
            for (int j = 0; j < pointNode.getChildNodes().getLength(); j++) {
                int id = Integer.parseInt(pointNode.getAttributes().getNamedItem(ID_NAME).getNodeValue());
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
                    case NEIGHBORS_NAME:
                        currentNeighbors = new ArrayList<>();
                        Node neighborsNode = pointNode.getChildNodes().item(j);
                        for (int n = 0; n < neighborsNode.getChildNodes().getLength(); n++) {
                            Node neighborNode = neighborsNode.getChildNodes().item(n);
                            currentNeighbors.add(Integer.parseInt(neighborNode.getAttributes().getNamedItem(ID_NAME).getNodeValue()));
                        }
                        break;
                }
                Point point = new ArrayPoint(0, dims);
                if (pointNode.getAttributes().getNamedItem(ROBUSTNESS_NAME) != null) {
                    robustnesses.put(id, new SimpleRobustness(FloatFactory.getObject(pointNode.getAttributes().getNamedItem(ROBUSTNESS_NAME))));
                }
                if (currentNeighbors != null) {
                    neighbors.put(id, currentNeighbors);
                }
                memory.put(id, point);
            }
        }

        PointWithNeighborhood[] points = new PointWithNeighborhood[robustnesses.size()];
        Robustness[] robustnessesFinal = new Robustness[robustnesses.size()];
        int index = 0;
        for (Entry<Integer, Robustness> entry : robustnesses.entrySet()) {
            PointWithNeighborhood point;
            if (neighbors.containsKey(entry.getKey())) {
                Collection<Point> neighborhood = new ArrayList<>();
                for (Integer id : neighbors.get(entry.getKey())) {
                    neighborhood.add(memory.get(id));
                }
                point = new PointWithNeigborhoodWrapper(memory.get(entry.getKey()), neighborhood);
            } else {
                point = new PointWithNeigborhoodWrapper(memory.get(entry.getKey()), Collections.EMPTY_LIST);
            }
            points[index] = point;
            robustnessesFinal[index] = entry.getValue();
            index++;
        }
        return new ArrayVerificationResult(points, robustnessesFinal, new SimpleRobustness(FloatFactory.getObject(source.getAttributes().getNamedItem(ROBUSTNESS_NAME))));
    }
}
