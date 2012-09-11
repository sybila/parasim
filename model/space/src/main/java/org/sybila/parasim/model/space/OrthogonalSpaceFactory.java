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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.sybila.parasim.model.math.Expression;
import org.sybila.parasim.model.math.Parameter;
import org.sybila.parasim.model.math.Variable;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.xml.FloatFactory;
import org.sybila.parasim.model.xml.XMLFormatException;
import org.sybila.parasim.model.xml.XMLRepresentableFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class OrthogonalSpaceFactory implements
        XMLRepresentableFactory<OrthogonalSpace> {
    public static final String SPACE_NAME = "space";
    public static final String VARIABLE_NAME = "variable";
    public static final String TIME_NAME = "time";
    public static final String ATTRIBUTE_MIN = "min";
    public static final String ATTRIBUTE_MAX = "max";
    public static final String PARAMETER_NAME = "parameter";
    public static final String ATTRIBUTE_NAME = "name";

    private final OdeSystem odeSystem;

    public OrthogonalSpaceFactory(OdeSystem odeSystem) {
        if (odeSystem == null) {
            throw new IllegalArgumentException("The parameter [odeSystem] is null.");
        }
        this.odeSystem = odeSystem;
    }

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
        Map<String, Float> paramMin = new HashMap<>();
        Map<String, Float> paramMax = new HashMap<>();
        Map<String, Float> varMin = new HashMap<>();
        Map<String, Float> varMax = new HashMap<>();

        for (int index = 0; index < children.getLength(); index++) {
            Node child = children.item(index);
            NamedNodeMap attr = child.getAttributes();
            switch (child.getNodeName()) {
                case VARIABLE_NAME:
                    {
                        String name = attr.getNamedItem(ATTRIBUTE_NAME).getNodeValue();
                        if (varMin.containsKey(name)) {
                            throw new XMLFormatException("The variable [" + name + "] is declared twice.");
                        }
                        float min = FloatFactory.getObject(attr.getNamedItem(ATTRIBUTE_MIN));
                        float max = FloatFactory.getObject(attr.getNamedItem(ATTRIBUTE_MAX));
                        if (min > max) {
                            throw new XMLFormatException("The min bound in for variable ["
                                    + name + "] is greater than max bound ("
                                    + min + ">" + max + ").");
                        }
                        varMin.put(name, min);
                        varMax.put(name, max);
                        break;
                    }
                case PARAMETER_NAME:
                    {
                        String name = attr.getNamedItem(ATTRIBUTE_NAME).getNodeValue();
                        if (paramMin.containsKey(name)) {
                            throw new IllegalStateException("The variable [" + name + "] is declared twice.");
                        }
                        float min = FloatFactory.getObject(attr.getNamedItem(ATTRIBUTE_MIN));
                        float max = FloatFactory.getObject(attr.getNamedItem(ATTRIBUTE_MAX));
                        if (min > max) {
                            throw new XMLFormatException("The min bound in for parameter ["
                                    + name + "] is greater than max bound ("
                                    + min + ">" + max + ").");
                        }
                        paramMin.put(name, min);
                        paramMax.put(name, max);
                        break;
                    }
                case TIME_NAME:
                    timeMin = FloatFactory.getObject(attr.getNamedItem(ATTRIBUTE_MIN));
                    timeMax = FloatFactory.getObject(attr.getNamedItem(ATTRIBUTE_MAX));
                    if (timeMin > timeMax) {
                        throw new XMLFormatException("The min bound in time is greater than max bound ("
                                + timeMin + ">" + timeMax + ").");
                    }
                    break;
                default:
                    throw new XMLFormatException("Unknown element: "
                            + child.getNodeName());
            }
        }
        Collection<Expression> parametersToRelease = new ArrayList<>(paramMax.size());
        for (String paramName: paramMax.keySet()) {
            parametersToRelease.add(odeSystem.getAvailableParameters().get(paramName));
        }
        OdeSystem releasedOdeSystem = odeSystem.release(parametersToRelease);

        float[] minArray = new float[releasedOdeSystem.dimension()];
        float[] maxArray = new float[releasedOdeSystem.dimension()];
        for (int dim=0; dim<releasedOdeSystem.dimension(); dim++) {
            if (odeSystem.isVariable(dim)) {
                Variable var = releasedOdeSystem.getVariable(dim);
                if (var == null) {
                    minArray[var.getIndex()] = releasedOdeSystem.getInitialVariableValue(var).getValue();
                    maxArray[var.getIndex()] = releasedOdeSystem.getInitialVariableValue(var).getValue();
                } else {
                    minArray[var.getIndex()] = varMin.get(var.getName());
                    maxArray[var.getIndex()] = varMax.get(var.getName());
                }
            } else if (releasedOdeSystem.isParamater(dim)) {
                Parameter param = releasedOdeSystem.getParameter(dim);
                if (param == null) {
                    minArray[param.getIndex()] = releasedOdeSystem.getDeclaredParamaterValue(param).getValue();
                    maxArray[param.getIndex()] = releasedOdeSystem.getDeclaredParamaterValue(param).getValue();
                } else {
                    minArray[param.getIndex()] = paramMin.get(param.getName());
                    maxArray[param.getIndex()] = paramMax.get(param.getName());
                }
            }
        }
        Point minBounds = new ArrayPoint(timeMin, minArray);
        Point maxBounds = new ArrayPoint(timeMax, maxArray);
        return new OrthogonalSpaceImpl(minBounds, maxBounds, releasedOdeSystem);
    }

}
