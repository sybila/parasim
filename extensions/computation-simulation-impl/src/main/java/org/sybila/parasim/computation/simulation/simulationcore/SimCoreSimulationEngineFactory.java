package org.sybila.parasim.computation.simulation.simulationcore;

import org.sybila.parasim.computation.simulation.cpu.SimulationEngine;
import org.sybila.parasim.computation.simulation.cpu.SimulationEngineFactory;

/**
 * @author Vojtech Bruza
 */
public class SimCoreSimulationEngineFactory implements SimulationEngineFactory {
    @Override
    public boolean isAvailable() {
        //TODO
        return false;
    }

    @Override
    public SimulationEngine simulationEngine(long stepLimit) {
        return new SimCoreSimulationEngine();
    }
}
