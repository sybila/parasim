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

import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.Point;
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
    public static final String DIMENSION_NAME = "dim";
    public static final String ROBUSTNESS_NAME = "robustness";

    public VerificationResult getObject(Node source) throws XMLFormatException {
        NodeList children = source.getChildNodes();

        int size = children.getLength();
        if (size == 0) {
            return new ArrayVerificationResult(0, new Point[0], new Robustness[0]);
        }

        Point[] points = new Point[size];
        Robustness[] robustness = new Robustness[size];
        int dimension = children.item(0).getChildNodes().getLength() - 1; //one of attributes is robustness

        for (int i = 0; i < size; i++) {
            NodeList dimNodes = children.item(i).getChildNodes();
            if (dimNodes.getLength() != dimension + 1) { //Necessary
                throw new XMLFormatException("Unexpected number of dimensions.");
            }
            try {
                float[] dims = new float[dimension];
                for (int j = 0; j < dimension; j++) {
                    dims[j] = FloatFactory.getObject(dimNodes.item(j).getFirstChild());
                }
                robustness[i] = new SimpleRobustness(FloatFactory.getObject(dimNodes.item(dimension).getFirstChild()));
                points[i] = new ArrayPoint(0, dims, 0, dimension);
            } catch (NumberFormatException nfe) {
                throw new XMLFormatException("Illegible number.", nfe);
            }
        }

        return new ArrayVerificationResult(size, points, robustness);
    }
}
