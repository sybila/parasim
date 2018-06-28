/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.computation.simulation.lsoda;

import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.computation.simulation.cpu.SimulationEngineFactory;
import org.sybila.parasim.model.ode.LsodaOdeSystem;
import org.sybila.parasim.model.trajectory.Point;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 * @author <a href="mailto:433392@fi.muni.cz">Vojtech Bruza</a>
 */
public class LsodaEngineFactory implements LsodaSimulationEngineFactory {

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public LsodaSimulationEngine simulationEngine(long stepLimit) {
        return (LsodaSimulationEngine) SimulationEngineFactory.THREAD_SIMULATION_ENGINE_MAP.computeIfAbsent(Thread.currentThread(), thread ->
                new LsodeEngine(stepLimit));
    }

    private static class LsodeEngine extends LsodaSimulationEngine {

        public LsodeEngine(long stepLimit) {
            super(stepLimit);
        }

        @Override
        protected float[] rawSimulation(Point point, LsodaOdeSystem odeSystem, long numberOfIterations, PrecisionConfiguration precision)
        throws Exception {
            float[] loadedData = lsodaWrapper.rawSimulation(point, odeSystem, numberOfIterations, precision);
            return loadedData;
        }
    }
}
