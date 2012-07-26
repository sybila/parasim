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
package org.sybila.parasim.core.extension.lifecycle.impl;

import java.lang.annotation.Annotation;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.core.context.Context;
import org.sybila.parasim.core.extension.configuration.api.ParasimDescriptor;
import org.sybila.parasim.core.extension.lifecycle.api.InstanceManager;
import org.sybila.parasim.core.extension.lifecycle.api.LifecycleRegistry;
import org.sybila.parasim.core.extension.lifecycle.spi.Configurator;
import org.sybila.parasim.core.extension.lifecycle.spi.Constructor;
import org.sybila.parasim.core.extension.lifecycle.spi.Destructor;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class InstanceManagerImpl implements InstanceManager {

    private final LifecycleRegistry registry;
    private final ParasimDescriptor descriptor;

    public InstanceManagerImpl(LifecycleRegistry registry, ParasimDescriptor descriptor) {
        Validate.notNull(registry);
        Validate.notNull(descriptor);
        this.registry = registry;
        this.descriptor = descriptor;
    }

    @Override
    public <T> T getInstance(Class<T> type, Class<? extends Annotation> qualifier, Context context) {
        Constructor<T, ?> constructor = registry.loadConstructor(type);
        if (constructor != null) {
            return getInstance(constructor, type, qualifier, context);
        } else {
            return null;
        }
    }

    @Override
    public <T> void destroyInstance(T instance) {
        Destructor<T> destructor = registry.loadDestructor((Class<T>) instance.getClass());
        if (destructor != null) {
            destructor.destroy(instance);
        }
    }

    private <T, C> T getInstance(Constructor<T, C> constructor, Class<T> type, Class<? extends Annotation> qualifier, Context context) {
        Configurator<T, C> configurator = (Configurator<T, C>) registry.loadConfigurator(type);
        C configuration = configurator == null ? null : configurator.configure(descriptor, qualifier);
        return constructor.create(configuration, qualifier);
    }

}
