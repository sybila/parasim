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
package org.sybila.parasim.computation.simulation;

import org.sybila.parasim.computation.simulation.api.AdaptiveStepConfiguration;
import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.computation.simulation.api.SimulatedDataBlock;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.Point;
import static org.testng.Assert.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractAdaptiveStepSimulationTest extends AbstractSimulatorTest<AdaptiveStepConfiguration, SimulatedDataBlock> {

    protected void testAbsoluteStep(int size) {
        SimulatedDataBlock result = getSimulator().simulate(getConfiguration(), createDataBlock(getConfiguration().getDimension(), size));
        for (int s = 0; s < size; s++) {
            Point previous = null;
            for (Point p : result.getTrajectory(s)) {
                if (previous == null) {
                    previous = p;
                    continue;
                }
                for(int dim=0; dim<p.getDimension(); dim++) {
                    assertTrue(Math.abs(previous.getValue(dim) - p.getValue(dim)) < getConfiguration().getSteps()[dim], "The absolute step condition in dimension <" + dim + "> doesn't hold. Found absolute step is <" + Math.abs(previous.getValue(dim) - p.getValue(dim)) + ">, expected <" + getConfiguration().getSteps()[dim] + ">");
                }
                previous = p;
            }
        }
    }

    @Override
    protected AdaptiveStepConfiguration createConfiguration() {
        final OdeSystem odeSystem = getOdeSystem(10);
        return new AdaptiveStepConfiguration() {

            private float[] maxAbsoluteError;
            private OrthogonalSpace space;
            private float[] steps;
            private PrecisionConfiguration precisionConfiguration = new PrecisionConfiguration() {

                public int getDimension() {
                    return 10;
                }

                public float getMaxAbsoluteError(int dim) {
                    if (maxAbsoluteError == null) {
                        maxAbsoluteError = new float[getDimension()];
                        for (int d = 0; d < getDimension(); d++) {
                            maxAbsoluteError[d] = 1;
                        }
                    }
                    return maxAbsoluteError[dim];
                }

                public float getMaxRelativeError() {
                    return (float) 0.1;
                }

                public Element toXML(Document doc) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            };

            @Override
            public int getDimension() {
                return 10;
            }

            @Override
            public int getMaxNumberOfIterations() {
                return 100;
            }

            @Override
            public OdeSystem getOdeSystem() {
                return odeSystem;
            }

            public OrthogonalSpace getSpace() {
                if (space == null) {
                    float[] minBounds = new float[getDimension()];
                    for (int dim = 0; dim < getDimension(); dim++) {
                        minBounds[dim] = 0;
                    }
                    float[] maxBounds = new float[getDimension()];
                    for (int dim = 0; dim < getDimension(); dim++) {
                        maxBounds[dim] = (dim + 1) * 10000;
                    }
                    space = new OrthogonalSpace(new ArrayPoint(0, minBounds), new ArrayPoint((float) 100, maxBounds));
                }
                return space;
            }
            
            @Override
            public float[] getSteps() {
                if (steps == null) {
                    steps = new float[getDimension()];
                    for (int dim = 0; dim < getDimension(); dim++) {
                        steps[dim] = 1;
                    }
                }
                return steps;
            }

            @Override
            public float getTimeStep() {
                return (float) 0.01;
            }

            public PrecisionConfiguration getPrecisionConfiguration() {
                return precisionConfiguration;
            }
        };
    }
}
