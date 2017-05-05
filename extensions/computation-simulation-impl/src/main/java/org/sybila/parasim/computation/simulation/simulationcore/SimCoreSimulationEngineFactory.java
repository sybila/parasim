package org.sybila.parasim.computation.simulation.simulationcore;

import org.sybila.parasim.computation.simulation.cpu.SimulationEngine;
import org.sybila.parasim.computation.simulation.cpu.SimulationEngineFactory;

/**
 * @author <a href="mailto:433392@fi.muni.cz">Vojtech Bruza</a>
 */
public class SimCoreSimulationEngineFactory implements SimulationEngineFactory {
    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public SimulationEngine simulationEngine(long stepLimit) {
        return new SimCoreSimulationEngine();
    }
}
