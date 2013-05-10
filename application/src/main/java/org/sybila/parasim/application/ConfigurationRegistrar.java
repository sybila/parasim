package org.sybila.parasim.application;

import org.sybila.parasim.application.model.ApplicationConfiguration;
import org.sybila.parasim.core.annotation.Provide;
import org.sybila.parasim.core.api.configuration.ExtensionDescriptor;
import org.sybila.parasim.core.api.configuration.ExtensionDescriptorMapper;
import org.sybila.parasim.core.api.configuration.ParasimDescriptor;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ConfigurationRegistrar {

    @Provide
    public ApplicationConfiguration registerApplicationConfiguration(ParasimDescriptor descriptor, ExtensionDescriptorMapper mapper) throws IllegalAccessException {
        ExtensionDescriptor extensionDescriptor = descriptor.getExtensionDescriptor("application");
        ApplicationConfiguration config = new ApplicationConfiguration();
        if (extensionDescriptor != null) {
            mapper.map(extensionDescriptor, config);
        }
        return config;
    }
}
