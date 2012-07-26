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
package org.sybila.parasim.core.extension.lifecycle;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.sybila.parasim.core.Instance;
import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.annotations.Observes;
import org.sybila.parasim.core.event.ManagerStarted;
import org.sybila.parasim.core.extension.configuration.api.ParasimDescriptor;
import org.sybila.parasim.core.extension.lifecycle.api.InstanceManager;
import org.sybila.parasim.core.extension.lifecycle.api.LifecycleRegistry;
import org.sybila.parasim.core.extension.lifecycle.impl.InstanceManagerImpl;
import org.sybila.parasim.core.extension.lifecycle.impl.LifecycleRegistryImpl;
import org.sybila.parasim.core.extension.lifecycle.spi.Configurator;
import org.sybila.parasim.core.extension.lifecycle.spi.Constructor;
import org.sybila.parasim.core.extension.lifecycle.spi.Destructor;
import org.sybila.parasim.core.spi.Sortable;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class LifecycleManagingExtension {

    private static Manager manager;

    @Inject
    private Instance<LifecycleRegistry> registry;
    @Inject
    private Instance<InstanceManager> instanceManager;

    public void startUsing(@Observes ManagerStarted event, Manager manager, ParasimDescriptor descriptor) {
        LifecycleManagingExtension.manager = manager;
        registry.set(createRegistry(manager));
        instanceManager.set(new InstanceManagerImpl(registry.get(), descriptor));
    }

    public LifecycleRegistry createRegistry(Manager manager) {
        LifecycleRegistry result = new LifecycleRegistryImpl();
        // constructors
        for (Constructor constructor: sort(manager.service(Constructor.class))) {
            Class<?> type = getFirstGenericParameterType(constructor.getClass(), Constructor.class);
            if (type != null) {
                result.storeConstructor(type, constructor);
            }
        }
        // configurators
        for (Configurator configurator: sort(manager.service(Configurator.class))) {
            Class<?> type = getFirstGenericParameterType(configurator.getClass(), Configurator.class);
            if (type != null) {
                result.storeConfigurator(type, configurator);
            }
        }
        // destructors
        for (Destructor destructor: sort(manager.service(Destructor.class))) {
            Class<?> type = getFirstGenericParameterType(destructor.getClass(), Destructor.class);
            if (type != null) {
                result.storeDestructor(type, destructor);
            }
        }
        return result;
    }

    public static InstanceManager getInstanceManager() {
        return manager == null ? null : manager.resolve(InstanceManager.class, Default.class, manager.getRootContext());
    }

    private static <T extends Sortable> List<T> sort(Collection<T> services) {
        List<T> result = new ArrayList<>(services);
        Collections.sort(result, new Comparator<Sortable>() {
            @Override
            public int compare(Sortable o1, Sortable o2) {
                return new Integer(o1.getPrecedence()).compareTo(new Integer(o2.getPrecedence()));
            }

        });
        return result;
    }

    private static Class<?> getFirstGenericParameterType(Class<?> clazz, Class<?> rawType) {
        for (Type interfaceType : clazz.getGenericInterfaces()) {
            if (interfaceType instanceof ParameterizedType) {
                ParameterizedType ptype = (ParameterizedType) interfaceType;
                if (rawType.isAssignableFrom((Class<?>) ptype.getRawType())) {
                    return (Class<?>) ptype.getActualTypeArguments()[0];
                }
            }
        }
        return null;
    }
}
