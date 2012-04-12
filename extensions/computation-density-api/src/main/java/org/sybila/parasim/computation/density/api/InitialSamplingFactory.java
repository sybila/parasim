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
package org.sybila.parasim.computation.density.api;

import org.sybila.parasim.model.xml.XMLFormatException;
import org.sybila.parasim.model.xml.XMLRepresentableFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class InitialSamplingFactory implements XMLRepresentableFactory<InitialSampling> {

    public static final String INITIAL_SAMPLING_NAME = "initial-sampling";
    public static final String DIMENSION_NAME = "dimension";
    public static final String DIMENSION_SAMPLING_NAME = "sampling";

    public InitialSampling getObject(Node source) throws XMLFormatException {
        if (!source.getNodeName().equals(INITIAL_SAMPLING_NAME)) {
            throw new XMLFormatException(
                    "Wrong name of precision element (expected `" + INITIAL_SAMPLING_NAME
                            + "', received `" + source.getNodeName() + "').");
        }
        NodeList children = source.getChildNodes();
        int dimension = children.getLength();
        int[] sampling = new int[dimension];
        for (int dim=0; dim<dimension; dim++) {
            Node child = children.item(dim);
            if (child.getNodeName().equals(DIMENSION_NAME)) {
                try {
                    sampling[dim] = Integer.parseInt(child.getAttributes().getNamedItem(DIMENSION_SAMPLING_NAME).getTextContent());
                } catch (NumberFormatException nfe) {
                    throw new XMLFormatException("Illegible number.", nfe);
                }
            } else {
                throw new XMLFormatException("Unknown element: " + child.getNodeName());
            }
        }
        return new ArrayInitialSampling(sampling);
    }
}
