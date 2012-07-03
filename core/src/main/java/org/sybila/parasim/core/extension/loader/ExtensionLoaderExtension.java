/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.core.extension.loader;

import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.annotations.Observes;
import org.sybila.parasim.core.event.ManagerProcessing;
import org.sybila.parasim.core.extension.loader.api.ExtensionBuilder;
import org.sybila.parasim.core.extension.loader.api.ExtensionLoader;
import org.sybila.parasim.core.LoadableExtension;
import org.sybila.parasim.core.ManagerImpl;
import org.sybila.parasim.core.extension.loader.impl.SPIExtensionLoader;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ExtensionLoaderExtension {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionLoaderExtension.class);

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
                LOGGER.debug("[" + extension.getName() + "] registered");
                event.extension(extension);
            }

            public Manager getManager() {
                return event.getManager();
            }

            @Override
            public <T> void service(Class<T> service, Class<? extends T> implementation) {
                ((ManagerImpl) event.getManager()).bindService(service, implementation);
            }
        };
    }
}
