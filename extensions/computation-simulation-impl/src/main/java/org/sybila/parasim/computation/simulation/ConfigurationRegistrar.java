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

import org.sybila.parasim.computation.lifecycle.api.annotations.ComputationInstanceScope;
import org.sybila.parasim.computation.simulation.api.AdaptiveStepConfiguration;
import org.sybila.parasim.computation.simulation.api.Configuration;
import org.sybila.parasim.computation.simulation.api.ImmutableAdaptiveStepConfiguraton;
import org.sybila.parasim.computation.simulation.api.ImmutableConfiguration;
import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.computation.simulation.api.annotations.SimulationSpace;
import org.sybila.parasim.core.annotation.Provide;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.space.OrthogonalSpace;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
@ComputationInstanceScope
public class ConfigurationRegistrar {

    @Provide
    public AdaptiveStepConfiguration registerAdaptiveStepConfiguration(Configuration configuration, PrecisionConfiguration precisionConfiguration) {
        return new ImmutableAdaptiveStepConfiguraton(
            configuration,
            precisionConfiguration
        );
    }

    @Provide
    public Configuration registerConfiguration(OdeSystem odeSystem, @SimulationSpace OrthogonalSpace space) {
        return new ImmutableConfiguration(odeSystem, space);
    }
}
