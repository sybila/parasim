package org.sybila.parasim.core.extension.loader;

import java.util.Collection;
import org.apache.log4j.Logger;
import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.annotations.Observes;
import org.sybila.parasim.core.event.ManagerProcessing;
import org.sybila.parasim.core.extension.loader.api.ExtensionBuilder;
import org.sybila.parasim.core.extension.loader.api.ExtensionLoader;
import org.sybila.parasim.core.LoadableExtension;
import org.sybila.parasim.core.extension.loader.impl.SPIExtensionLoader;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ExtensionLoaderExtension {

    private static final Logger LOGGER = Logger.getLogger(ExtensionLoaderExtension.class);

    private ExtensionLoader extensionLoader;

    public ExtensionLoaderExtension(ExtensionLoader extensionLoader) {
        if (extensionLoader == null) {
            throw new IllegalArgumentException("The parameter [extensionLoader] is null.");
        }
        this.extensionLoader = extensionLoader;
    }

    public ExtensionLoaderExtension() {
        this(new SPIExtensionLoader());
    }

    public void load(@Observes ManagerProcessing event) {
        ExtensionBuilder builder = createExtensionBuilder(event);
        for (LoadableExtension extension: locateExtensions()) {
            LOGGER.info("Loading " + extension.getClass().getName());
            try {
                extension.register(builder);
            } catch(Exception e) {
                LOGGER.warn("Loading failed.", e);
            }
        }
    }

    private Collection<LoadableExtension> locateExtensions() {
        return extensionLoader.load();
    }

    private ExtensionBuilder createExtensionBuilder(final ManagerProcessing event) {
        return new ExtensionBuilder() {

            public void extension(Class<?> extension) {
                LOGGER.info("[" + extension.getName() + "] registered");
                event.extension(extension);
            }

            public Manager getManager() {
                return event.getManager();
            }
        };
    }
}
