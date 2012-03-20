package org.sybila.parasim.computation.simulation;

import org.sybila.parasim.computation.simulation.api.AdaptiveStepSimulator;
import org.sybila.parasim.computation.simulation.cpu.OctaveSimulator;
import org.sybila.parasim.computation.simulation.cpu.Rkf45Simulator;
import org.sybila.parasim.core.Instance;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.annotations.Observes;
import org.sybila.parasim.core.extension.configuration.api.ParasimDescriptor;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class SimulatorRegistrar {
    
    @Inject
    private Instance<AdaptiveStepSimulator> simulator;
    
    public void register(@Observes ParasimDescriptor descriptor) {
        if (OctaveSimulator.isAvailable()) {
            simulator.set(new OctaveSimulator());
        } else {
            simulator.set(new Rkf45Simulator());
        }
    }
}
