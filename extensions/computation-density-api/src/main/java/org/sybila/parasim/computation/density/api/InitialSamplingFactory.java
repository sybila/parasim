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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.sybila.parasim.model.math.Expression;
import org.sybila.parasim.model.math.Parameter;
import org.sybila.parasim.model.math.Variable;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.xml.XMLFormatException;
import org.sybila.parasim.model.xml.XMLRepresentableFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class InitialSamplingFactory implements XMLRepresentableFactory<InitialSampling> {

    public static final String INITIAL_SAMPLING_NAME = "initial-sampling";
    public static final String VARIABLE_NAME = "variable";
    public static final String PARAMETER_NAME = "parameter";
    public static final String ATTRIBUTE_NAME = "name";
    public static final String ATTRIBUTE_SAMPLING = "sampling";
    private final OdeSystem odeSystem;

    public InitialSamplingFactory(OdeSystem odeSystem) {
        if (odeSystem == null) {
            throw new IllegalArgumentException("The parameter [odeSystem] is null.");
        }
        this.odeSystem = odeSystem;
    }

    @Override
    public InitialSampling getObject(Node source) throws XMLFormatException {
        if (!source.getNodeName().equals(INITIAL_SAMPLING_NAME)) {
            throw new XMLFormatException(
                    "Wrong name of precision element (expected `" + INITIAL_SAMPLING_NAME
                    + "', received `" + source.getNodeName() + "').");
        }
        Map<String, Integer> varSampling = new HashMap<>();
        Map<String, Integer> paramSampling = new HashMap<>();
        Collection<Expression> toRelease = new ArrayList<>();
        NodeList children = source.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            switch (child.getNodeName()) {
                case VARIABLE_NAME: {
                    int s = Integer.parseInt(child.getAttributes().getNamedItem(ATTRIBUTE_SAMPLING).getTextContent());
                    String name = child.getAttributes().getNamedItem(ATTRIBUTE_NAME).getTextContent();
                    varSampling.put(name, s);
                    break;
                }
                case PARAMETER_NAME: {
                    int s = Integer.parseInt(child.getAttributes().getNamedItem(ATTRIBUTE_SAMPLING).getTextContent());
                    String name = child.getAttributes().getNamedItem(ATTRIBUTE_NAME).getTextContent();
                    paramSampling.put(name, s);
                    Parameter parameter = odeSystem.getAvailableParameters().get(name);
                    if (parameter.isSubstituted()) {
                        toRelease.add(odeSystem.getAvailableParameters().get(name));
                    }
                    break;
                }
                default:
                    throw new XMLFormatException("Unknown element: " + child.getNodeName());
            }
        }

        OdeSystem releasedOdeSystem = odeSystem.release(toRelease);
        int[] sampling = new int[releasedOdeSystem.dimension()];

        for (int dim = 0; dim < releasedOdeSystem.dimension(); dim++) {
            if (releasedOdeSystem.isVariable(dim)) {
                Variable var = releasedOdeSystem.getVariable(dim);
                sampling[dim] = varSampling.containsKey(var.getName()) ? varSampling.get(var.getName()) : 1;
            } else {
                Parameter param = releasedOdeSystem.getParameter(dim);
                sampling[dim] = paramSampling.containsKey(param.getName()) ? paramSampling.get(param.getName()) : 1;
            }
        }

        return new ArrayInitialSampling(releasedOdeSystem, sampling);
    }
}
