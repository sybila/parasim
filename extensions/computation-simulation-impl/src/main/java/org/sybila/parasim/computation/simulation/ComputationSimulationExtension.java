package org.sybila.parasim.computation.simulation;

import org.sybila.parasim.core.LoadableExtension;
import org.sybila.parasim.core.extension.loader.api.ExtensionBuilder;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ComputationSimulationExtension implements LoadableExtension {

    public void register(ExtensionBuilder builder) {
        builder.extension(SimulatorRegistrar.class);
        builder.extension(ConfigurationRegistrar.class);
        builder.extension(ExtensionConfigurator.class);
    }

}
