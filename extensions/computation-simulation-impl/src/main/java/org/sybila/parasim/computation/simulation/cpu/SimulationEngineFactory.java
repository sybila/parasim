package org.sybila.parasim.computation.simulation.cpu;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface SimulationEngineFactory {

    boolean isAvailable();

    SimulationEngine simulationEngine(long stepLimit, double relativeTolerance);

}
