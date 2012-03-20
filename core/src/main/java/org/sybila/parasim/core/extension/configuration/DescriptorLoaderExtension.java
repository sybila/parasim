package org.sybila.parasim.core.extension.configuration;

import org.sybila.parasim.core.LoadableExtension;
import org.sybila.parasim.core.extension.configuration.impl.DescriptorLoaderRegistrar;
import org.sybila.parasim.core.extension.loader.api.ExtensionBuilder;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class DescriptorLoaderExtension implements LoadableExtension {

    public void register(ExtensionBuilder builder) {
        builder.extension(DescriptorLoaderRegistrar.class);
    }
    
}
