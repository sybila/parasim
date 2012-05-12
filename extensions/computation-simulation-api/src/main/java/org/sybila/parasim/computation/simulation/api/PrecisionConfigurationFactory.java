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
package org.sybila.parasim.computation.simulation.api;

import org.sybila.parasim.model.xml.XMLFormatException;
import org.sybila.parasim.model.xml.XMLRepresentableFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class PrecisionConfigurationFactory implements XMLRepresentableFactory<PrecisionConfiguration> {

    public static final String DIMENSION_NAME = "dimension";
    public static final String MAX_ABSOLUTE_ERROR_NAME = "maxAbsoluteError";
    public static final String MAX_RELATIVE_ERROR_NAME = "maxRelativeError";
    public static final String PRECISION_NAME = "precision";
    public static final String TIME_STEP_NAME = "timeStep";

    public PrecisionConfiguration getObject(Node source) throws XMLFormatException {
        if (!source.getNodeName().equals(PRECISION_NAME)) {
            throw new XMLFormatException(
                    "Wrong name of precision element (expected `" + PRECISION_NAME
                            + "', received `" + source.getNodeName() + "').");
        }
        NodeList children = source.getChildNodes();
        int dimension = children.getLength();
        float maxRelativeError;
        try {
             maxRelativeError = Float.parseFloat(source.getAttributes().getNamedItem(MAX_RELATIVE_ERROR_NAME).getTextContent());
        } catch (NumberFormatException nfe) {
            throw new XMLFormatException("Illegible number.", nfe);
        }
        float timeStep;
        try {
            timeStep = Float.parseFloat(source.getAttributes().getNamedItem(TIME_STEP_NAME).getTextContent());
        } catch (NumberFormatException nfe) {
            throw new XMLFormatException("Illegible number.", nfe);
        }

        float[] maxAbsoluteError = new float[dimension];
        for (int index = 0; index < dimension; index++) {
            Node child = children.item(index);
            if (child.getNodeName().equals(DIMENSION_NAME)) {
                try {
                    maxAbsoluteError[index] = Float.parseFloat(child.getAttributes().getNamedItem(MAX_ABSOLUTE_ERROR_NAME).getTextContent());
                } catch (NumberFormatException nfe) {
                    throw new XMLFormatException("Illegible number.", nfe);
                }
            } else {
                throw new XMLFormatException("Unknown element: " + child.getNodeName());
            }
        }
        return new ArrayPrecisionConfiguration(maxAbsoluteError, maxRelativeError, timeStep);
    }
}
