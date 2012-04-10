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

import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.space.OrthogonalSpace;

/*
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ImmutableAdaptiveStepConfiguraton implements AdaptiveStepConfiguration {

    private Configuration configuration;
    private float[] maxAbsoluteError;
    private float maxRelativeError;

    public ImmutableAdaptiveStepConfiguraton(Configuration configuration, float[] maxAbsoluteError, float maxRelativeError) {
        if (configuration == null) {
            throw new IllegalArgumentException("The parameter configuration is null.");
        }
        if (maxAbsoluteError == null) {
            throw new IllegalArgumentException("The parameter maxAbsoluteError is null.");
        }
        if (configuration.getDimension() != maxAbsoluteError.length) {
            throw new IllegalArgumentException("The number of dimensions doesn't match with size of maxAbsoluteError array.");
        }
        this.configuration = configuration;
        this.maxAbsoluteError = maxAbsoluteError;
        this.maxRelativeError = maxRelativeError;
    }

    @Override
    public float[] getMaxAbsoluteError() {
        return maxAbsoluteError;
    }

    @Override
    public float getMaxRelativeError() {
        return maxRelativeError;
    }

    @Override
    public int getDimension() {
        return configuration.getDimension();
    }

    @Override
    public int getMaxNumberOfIterations() {
        return configuration.getMaxNumberOfIterations();
    }

    @Override
    public OdeSystem getOdeSystem() {
        return configuration.getOdeSystem();
    }

    @Override
    public float[] getSteps() {
        return configuration.getSteps();
    }

    @Override
    public float getTimeStep() {
        return configuration.getTimeStep();
    }

    public OrthogonalSpace getSpace() {
        return configuration.getSpace();
    }

}
