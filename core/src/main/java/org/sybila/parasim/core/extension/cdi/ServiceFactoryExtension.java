package org.sybila.parasim.core.extension.cdi;

import org.sybila.parasim.core.LoadableExtension;
import org.sybila.parasim.core.extension.cdi.impl.ServiceFactoryRegistrar;
import org.sybila.parasim.core.extension.loader.api.ExtensionBuilder;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ServiceFactoryExtension implements LoadableExtension {

    public void register(ExtensionBuilder builder) {
        builder.extension(ServiceFactoryRegistrar.class);
    }

}
