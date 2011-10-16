package org.sybila.parasim.computation.simulation;

import org.sybila.parasim.computation.Module;
import org.sybila.parasim.computation.ModuleComputationException;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class SimulationModule<Conf extends Configuration> implements Module<org.sybila.parasim.computation.DataBlock, SimulatedDataBlock<Trajectory>> {

    private Conf configuration;
    private Simulator<Conf, SimulatedDataBlock<Trajectory>> simulator;
    
    public SimulationModule(Simulator<Conf, SimulatedDataBlock<Trajectory>> simulator, Conf configuration) {
        if (simulator == null) {
            throw new IllegalArgumentException("The parameter simulator is null.");
        }
        if (configuration == null) {
            throw new IllegalArgumentException("The parameter configuration is null.");
        }
        this.simulator = simulator;
        this.configuration = configuration;
    }
    
    /**
     * Returns a data block containing trajectory of generated points.
     * 
     * @param input trajectories whose last points are used as seeds for simulation
     * @return data block containing trajectory of generated points.
     * @throws ModuleComputationException if the simulation fails
     */
    @Override
    public SimulatedDataBlock<Trajectory> compute(org.sybila.parasim.computation.DataBlock input) throws ModuleComputationException {
          return simulator.simulate(configuration, input);
    }
    
    /**
     * Returns configuration used for simulation
     * 
     * @return  configuration
     */
    public Conf getConfiguration() {
        return configuration;
    }
    
    /**
     * Sets a configurations used for simulation
     * 
     * @param configuration 
     */
    public void setConfiguration(Conf configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("The parameter configuration is null.");
        }
        this.configuration = configuration;
    }
    
}
