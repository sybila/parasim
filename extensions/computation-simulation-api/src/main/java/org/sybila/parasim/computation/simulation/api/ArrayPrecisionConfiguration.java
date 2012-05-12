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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ArrayPrecisionConfiguration implements PrecisionConfiguration {

    private final float[] maxAbsoluteError;
    private final float maxRelativeError;
    private final float timeStep;

    public ArrayPrecisionConfiguration(final float[] maxAbsoluteError, final float maxRelativeError, final float timeStep) {
        if (maxAbsoluteError == null) {
            throw new IllegalArgumentException("The parameter [maxAbsoluteError] is null.");
        }
        if (maxRelativeError < 0) {
            throw new IllegalArgumentException("The parameter [maxRelativeError] has to be a positive number.");
        }
        if (timeStep < 0) {
            throw new IllegalArgumentException("The parameter [timeStep] has to be a positibe number.");
        }
        this.timeStep = timeStep;
        this.maxAbsoluteError = maxAbsoluteError;
        this.maxRelativeError = maxRelativeError;
    }

    public int getDimension() {
        return maxAbsoluteError.length;
    }

    public float getMaxAbsoluteError(int dim) {
        return maxAbsoluteError[dim];
    }

    public float getMaxRelativeError() {
        return maxRelativeError;
    }

    public float getTimeStep() {
        return timeStep;
    }

    public Element toXML(Document doc) {
        Element precision = doc.createElement(PrecisionConfigurationFactory.PRECISION_NAME);
        precision.setAttribute(PrecisionConfigurationFactory.MAX_RELATIVE_ERROR_NAME, Float.toString(getMaxRelativeError()));
        precision.setAttribute(PrecisionConfigurationFactory.TIME_STEP_NAME, Float.toString(getTimeStep()));
        for (int dim=0; dim<getDimension(); dim++) {
            Element dimension = doc.createElement(PrecisionConfigurationFactory.DIMENSION_NAME);
            dimension.setAttribute(PrecisionConfigurationFactory.MAX_ABSOLUTE_ERROR_NAME, Float.toString(getMaxAbsoluteError(dim)));
            precision.appendChild(dimension);
        }
        return precision;
    }
}
