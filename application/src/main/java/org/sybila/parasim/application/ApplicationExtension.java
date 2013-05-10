package org.sybila.parasim.application;

import org.sybila.parasim.core.api.loader.ExtensionBuilder;
import org.sybila.parasim.core.spi.LoadableExtension;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ApplicationExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        builder.extension(ConfigurationRegistrar.class);
    }
}
