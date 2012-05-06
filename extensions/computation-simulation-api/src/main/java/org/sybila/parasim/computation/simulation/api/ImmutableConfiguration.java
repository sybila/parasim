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

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ImmutableConfiguration implements Configuration {

    public static final int DEFAULT_MAX_NUMBER_OF_ITERATIONS = 100000;
    public static final float DEFAULT_STEP = (float) 0.1;
    public static final float DEFAULT_TIME_STEP = (float) 0.001;
    private int maxNumberOfIterations;
    private OdeSystem odeSystem;
    private OrthogonalSpace space;
    private float[] steps;
    private float timeStep;

    public ImmutableConfiguration(OdeSystem odeSystem, OrthogonalSpace space) {
        this(
            odeSystem,
            space,
            getConstantArray(odeSystem.dimension(), DEFAULT_STEP),
            (DEFAULT_TIME_STEP > space.getMaxBounds().getTime() - space.getMinBounds().getTime() ? space.getMaxBounds().getTime() - space.getMinBounds().getTime() : DEFAULT_TIME_STEP),
            DEFAULT_MAX_NUMBER_OF_ITERATIONS
        );
    }

    public ImmutableConfiguration(OdeSystem odeSystem, OrthogonalSpace space, float[] steps, float timeStep, int maxNumberOfIterations) {
        if (odeSystem == null) {
            throw new IllegalArgumentException("The paremeter odeSystem is null.");
        }
        if (space == null) {
            throw new IllegalArgumentException("The paremeter space is null.");
        }
        if (steps == null) {
            throw new IllegalArgumentException("The paremeter steps is null.");
        }
        if (odeSystem.dimension() != space.getDimension()) {
            throw new IllegalArgumentException("The number of ODE system dimensions ("+odeSystem.dimension()+") doesn't match with simulation space dimension ("+space.getDimension()+")");
        }
        if (odeSystem.dimension() != steps.length) {
            throw new IllegalArgumentException("The number of ODE system dimensions doesn't match with steps size.");
        }
        if (timeStep <= 0) {
            throw new IllegalArgumentException("The time step has to be a positive number.");
        }
        if (maxNumberOfIterations <= 0) {
            throw new IllegalArgumentException("The max number of iterations has to be a positive number.");
        }
        if (timeStep > space.getMaxBounds().getTime() - space.getMinBounds().getTime()) {
            throw new IllegalArgumentException("The time step can't be higher than target time");
        }
        this.odeSystem = odeSystem;
        this.space = space;
        this.steps = steps;
        this.timeStep = timeStep;
        this.maxNumberOfIterations = maxNumberOfIterations;

    }

    @Override
    public int getDimension() {
        return odeSystem.dimension();
    }

    @Override
    public int getMaxNumberOfIterations() {
        return maxNumberOfIterations;
    }

    @Override
    public OdeSystem getOdeSystem() {
        return odeSystem;
    }

    @Override
    public float[] getSteps() {
        return steps;
    }

    @Override
    public float getTimeStep() {
        return timeStep;
    }

    private static float[] getConstantArray(int size, float number) {
        float[] result = new float[size];
        for (int i = 0; i < size; i++) {
            result[i] = number;
        }
        return result;
    }

    public OrthogonalSpace getSpace() {
        return space;
    }
}
