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
package org.sybila.parasim.computation.simulation.cpu;

import org.sybila.parasim.computation.simulation.AbstractAdaptiveStepSimulationTest;
import org.sybila.parasim.computation.simulation.api.AdaptiveStepConfiguration;
import org.sybila.parasim.computation.simulation.api.Simulator;
import org.sybila.parasim.computation.simulation.octave.LsodeEngineFactory;
import org.testng.SkipException;
import org.testng.annotations.Test;

/**
 *
 * @author jpapouse
 */
public class TestSimpleAdaptiveStepSimulator extends AbstractAdaptiveStepSimulationTest {

    private static final SimulationEngineFactory LSODE_ENGINE_FACTORY = new LsodeEngineFactory();

    @Test
    public void testTimeStep() {
        if (!LSODE_ENGINE_FACTORY.isAvailable()) {
            throw new SkipException("The Octave is not available.");
        }
        super.testTimeStep(10, 10);
    }

    @Test
    public void testMinimalNumberOfPoints() {
        if (!LSODE_ENGINE_FACTORY.isAvailable()) {
            throw new SkipException("The Octave is not available.");
        }
        super.testMinimalNumberOfPoints(10, 10);
    }

    @Test
    public void testValidNumberOfTrajectories() {
        if (!LSODE_ENGINE_FACTORY.isAvailable()) {
            throw new SkipException("The Octave is not available.");
        }
        super.testValidNumberOfTrajectories(10, 10);
    }

    @Test
    public void testParameters() {
        if (!LSODE_ENGINE_FACTORY.isAvailable()) {
            throw new SkipException("The Octave is not available.");
        }
        super.testParameters(2);
    }

    @Override
    protected Simulator<AdaptiveStepConfiguration> createSimulator() {
        return new SimpleAdaptiveStepSimulator(LSODE_ENGINE_FACTORY);
    }

}
