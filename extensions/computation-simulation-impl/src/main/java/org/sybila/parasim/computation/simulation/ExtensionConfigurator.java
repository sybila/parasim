package org.sybila.parasim.computation.simulation;

import org.sybila.parasim.core.Instance;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.annotations.Observes;
import org.sybila.parasim.core.extension.configuration.api.ExtensionDescriptorMapper;
import org.sybila.parasim.core.extension.configuration.api.ParasimDescriptor;
import org.sybila.parasim.core.extension.configuration.api.event.ConfigurationLoaded;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ExtensionConfigurator {

    @Inject
    private Instance<ExtensionConfiguration> extensionConfiguration;

    public void configure(@Observes ConfigurationLoaded event, ParasimDescriptor descriptor, ExtensionDescriptorMapper mapper) throws IllegalAccessException {
        ExtensionConfiguration configuration = new ExtensionConfiguration();
        mapper.map(descriptor.getExtensionDescriptor("simulator"), configuration);
        configuration.validate();
        extensionConfiguration.set(configuration);
    }
}
