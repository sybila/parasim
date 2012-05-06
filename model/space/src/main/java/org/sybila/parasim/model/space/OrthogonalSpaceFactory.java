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
package org.sybila.parasim.model.space;

import java.util.ArrayList;
import java.util.List;
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
    public static final String TIME_NAME = "time";
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
        float timeMin = 0;
        float timeMax = 0;
        List<Float> min = new ArrayList<Float>();
        List<Float> max = new ArrayList<Float>();

        for (int index = 0; index < children.getLength(); index++) {
            Node child = children.item(index);
            if (child.getNodeName().equals(DIMENSION_NAME)) {
                NamedNodeMap attr = child.getAttributes();
                try {
                    min.add(Float.valueOf(attr.getNamedItem(ATTRIBUTE_MIN)
                            .getNodeValue()));
                    max.add(Float.valueOf(attr.getNamedItem(ATTRIBUTE_MAX)
                            .getNodeValue()));
                } catch (NumberFormatException nfe) {
                    throw new XMLFormatException("Illegible number.", nfe);
                }
                if (min.get(min.size() - 1) > max.get(max.size() - 1)) {
                    throw new XMLFormatException("The min bound in dimension <"
                            + (max.size() - 1) + "> is greater than max bound ("
                            + min.get(min.size() - 1) + ">" + max.get(max.size() - 1) + ").");
                }
            } else if (child.getNodeName().equals(TIME_NAME)) {
                NamedNodeMap attr = child.getAttributes();
                try {
                    timeMin = Float.valueOf(attr.getNamedItem(ATTRIBUTE_MIN)
                            .getNodeValue());
                    timeMax = Float.valueOf(attr.getNamedItem(ATTRIBUTE_MAX)
                            .getNodeValue());
                } catch (NumberFormatException nfe) {
                    throw new XMLFormatException("Illegible number.", nfe);
                }
                if (timeMin > timeMax) {
                    throw new XMLFormatException("The min bound in time is greater than max bound ("
                            + timeMin + ">" + timeMax + ").");
                }
            } else {
                throw new XMLFormatException("Unknown element: "
                        + child.getNodeName());
            }
        }
        float[] minArray = new float[min.size()];
        float[] maxArray = new float[max.size()];
        for (int i=0; i<min.size(); i++) {
            minArray[i] = min.get(i);
            maxArray[i] = max.get(i);
        }
        Point minBounds = new ArrayPoint(timeMin, minArray);
        Point maxBounds = new ArrayPoint(timeMax, maxArray);
        return new OrthogonalSpace(minBounds, maxBounds);
    }

}
