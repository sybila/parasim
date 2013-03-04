/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.core.impl.loader;

import org.sybila.parasim.core.api.loader.ExtensionBuilder;
import org.sybila.parasim.core.common.ReflectionUtils;
import org.sybila.parasim.core.impl.ExtensionStorage;
import org.sybila.parasim.core.impl.ServiceStorage;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ExtensionBuilderImpl implements ExtensionBuilder {

    private final ServiceStorage serviceStorage;
    private final ExtensionStorage extensionStorage;

    public ExtensionBuilderImpl(ServiceStorage serviceStorage, ExtensionStorage extensionStorage) {
        this.serviceStorage = serviceStorage;
        this.extensionStorage = extensionStorage;
    }

    @Override
    public void extension(Class<?> extension) {
        extensionStorage.store(extension);
    }

    @Override
    public <T> void service(Class<T> service, Class<? extends T> implementation) {
        try {
            serviceStorage.store(service, ReflectionUtils.createInstance(implementation));
        } catch (Exception e) {
            throw new IllegalStateException("Can't register service <" + implementation + ">.", e);
        }
    }

}
