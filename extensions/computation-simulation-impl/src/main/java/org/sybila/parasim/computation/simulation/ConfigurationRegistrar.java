package org.sybila.parasim.computation.simulation;

import org.apache.commons.lang3.ArrayUtils;
import org.sybila.parasim.computation.lifecycle.api.annotations.ComputationScope;
import org.sybila.parasim.computation.simulation.api.AdaptiveStepConfiguration;
import org.sybila.parasim.computation.simulation.api.Configuration;
import org.sybila.parasim.computation.simulation.api.ImmutableAdaptiveStepConfiguraton;
import org.sybila.parasim.computation.simulation.api.ImmutableConfiguration;
import org.sybila.parasim.core.annotations.Provide;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.space.OrthogonalSpace;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
@ComputationScope
public class ConfigurationRegistrar {

    @Provide
    public AdaptiveStepConfiguration registerAdaptiveStepConfiguration(Configuration configuration, ExtensionConfiguration extensionConfiguration) {
       return new ImmutableAdaptiveStepConfiguraton(
            configuration,
            ArrayUtils.toPrimitive(extensionConfiguration.getMaxAbsoluteError()),
            extensionConfiguration.getMaxRelativeError()
        );
    }

    @Provide
    public Configuration registerConfiguration(OdeSystem odeSystem, OrthogonalSpace space) {
        return new ImmutableConfiguration(odeSystem, space);
    }
}
