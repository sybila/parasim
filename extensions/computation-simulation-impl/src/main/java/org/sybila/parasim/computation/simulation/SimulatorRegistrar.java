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
package org.sybila.parasim.computation.simulation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.computation.lifecycle.api.annotations.ComputationInstanceScope;
import org.sybila.parasim.computation.simulation.api.AdaptiveStepSimulator;
import org.sybila.parasim.computation.simulation.cpu.SimpleAdaptiveStepSimulator;
import org.sybila.parasim.computation.simulation.lsoda.LsodaEngineFactory;
import org.sybila.parasim.computation.simulation.lsoda.LsodaSimulationEngineFactory;
import org.sybila.parasim.core.annotation.Provide;
import org.sybila.parasim.core.api.configuration.ExtensionDescriptor;
import org.sybila.parasim.core.api.configuration.ExtensionDescriptorMapper;
import org.sybila.parasim.core.api.configuration.ParasimDescriptor;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
@ComputationInstanceScope
public class SimulatorRegistrar {

    private final Logger LOGGER = LoggerFactory.getLogger(SimulatorRegistrar.class);

    @Provide
    public AdaptiveStepSimulator registerAdaptiveStepSimulator(ComputationSimulationConfiguration configuration, LsodaSimulationEngineFactory lsodaSimulationEngineFactory) {
            return new SimpleAdaptiveStepSimulator(lsodaSimulationEngineFactory);
    }

    @Provide
    public ComputationSimulationConfiguration registerConfiguration(ParasimDescriptor descriptor, ExtensionDescriptorMapper mapper) throws IllegalAccessException {
        ExtensionDescriptor extensionDescriptor = descriptor.getExtensionDescriptor("simulation");
        ComputationSimulationConfiguration configuration = new ComputationSimulationConfiguration();
        if (extensionDescriptor != null) {
            mapper.map(extensionDescriptor, configuration);
        }
        return configuration;
    }

    @Provide
    public LsodaSimulationEngineFactory provideLsodaSimulationEngineFactory(ComputationSimulationConfiguration configuration) {
            LOGGER.debug("using default LSODE simulation engine");
            return new LsodaEngineFactory();
    }
}
