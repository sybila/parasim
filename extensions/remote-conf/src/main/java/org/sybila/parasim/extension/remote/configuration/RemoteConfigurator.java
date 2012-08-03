package org.sybila.parasim.extension.remote.configuration;

import org.sybila.parasim.core.annotations.Provide;
import org.sybila.parasim.core.extension.configuration.api.ExtensionDescriptor;
import org.sybila.parasim.core.extension.configuration.api.ExtensionDescriptorMapper;
import org.sybila.parasim.core.extension.configuration.api.ParasimDescriptor;

/**
 * @author <a href="mailto:xpapous1@mail.muni.cz">Jan Papousek</a>
 */
public class RemoteConfigurator {

    @Provide
    public RemoteConfiguration provideConfiguration(ParasimDescriptor descriptor, ExtensionDescriptorMapper mapper) throws IllegalAccessException {
        RemoteConfiguration remoteConfiguration = new RemoteConfiguration();
        ExtensionDescriptor extensionDescriptor = descriptor.getExtensionDescriptor("remote");
        if (descriptor != null) {
            mapper.map(extensionDescriptor, remoteConfiguration);
        }
        remoteConfiguration.validate();
        return remoteConfiguration;
    }

}
