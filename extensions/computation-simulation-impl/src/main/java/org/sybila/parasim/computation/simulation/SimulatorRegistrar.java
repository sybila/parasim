package org.sybila.parasim.computation.simulation;

import org.sybila.parasim.computation.simulation.api.AdaptiveStepSimulator;
import org.sybila.parasim.computation.simulation.cpu.OctaveSimulator;
import org.sybila.parasim.computation.simulation.cpu.Rkf45Simulator;
import org.sybila.parasim.core.annotations.Provide;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class SimulatorRegistrar {

    @Provide
    public AdaptiveStepSimulator registerAdaptiveStepSimulator() {
        if (OctaveSimulator.isAvailable()) {
            return new OctaveSimulator();
        } else {
            return new Rkf45Simulator();
        }
    }
}
