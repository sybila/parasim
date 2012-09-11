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

import org.sybila.parasim.model.ode.OdeSystem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ArrayInitialSampling implements InitialSampling {

    private final int[] sampling;
    private final OdeSystem odeSystem;

    public ArrayInitialSampling(OdeSystem odeSystem, int... sampling) {
        if (sampling == null) {
            throw new IllegalArgumentException("The parameter [sampling] is null.");
        }
        if (sampling.length == 0) {
            throw new IllegalArgumentException("The dimension of sampling has to be a positive number.");
        }
        if (odeSystem == null) {
            throw new IllegalArgumentException("The parameter [odeSystem] is null.");
        }
        for (int dim=0; dim<sampling.length; dim++) {
            if (sampling[dim] <= 0) {
                throw new IllegalArgumentException("The number of samples has to be a positive number.");
            }
        }
        this.sampling = sampling;
        this.odeSystem = odeSystem;
    }

    @Override
    public int getDimension() {
        return sampling.length;
    }

    @Override
    public int getNumberOfSamples(int dim) {
        return sampling[dim];
    }

    @Override
    public OdeSystem getOdeSystem() {
        return odeSystem;
    }

    @Override
    public Element toXML(Document doc) {
        Element initialSampling = doc.createElement(InitialSamplingFactory.INITIAL_SAMPLING_NAME);
        for (int dim=0; dim<odeSystem.dimension(); dim++) {
            String elementName;
            String name;
            if (odeSystem.isVariable(dim)) {
                elementName = InitialSamplingFactory.VARIABLE_NAME;
                name = odeSystem.getVariable(dim).getName();
            } else {
                elementName = InitialSamplingFactory.PARAMETER_NAME;
                name = odeSystem.getParameter(dim).getName();
            }
            if (sampling[dim] == 1) {
                continue;
            }
            Element dimension = doc.createElement(elementName);
            dimension.setAttribute(InitialSamplingFactory.ATTRIBUTE_SAMPLING, Integer.toString(sampling[dim]));
            dimension.setAttribute(InitialSamplingFactory.ATTRIBUTE_NAME, name);
            initialSampling.appendChild(dimension);
        }
        return initialSampling;
    }
}
