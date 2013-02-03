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
package org.sybila.parasim.core.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import net.jcip.annotations.GuardedBy;
import org.sybila.parasim.core.api.ServiceRepository;
import org.sybila.parasim.core.spi.Sortable;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ServiceStorage implements ServiceRepository {

    private final Map<Class<?>, Collection<?>> instances = new ConcurrentHashMap<>();
    private final Map<Class<?>, Collection<Class<?>>> classes = new ConcurrentHashMap<>();

    public <T> Collection<T> load(Class<T> service) {
        Collection<T> result = (Collection<T>) instances.get(service);
        if (result == null) {
            return new ArrayList<>();
        } else {
            return Collections.unmodifiableCollection(result);
        }
    }

    @GuardedBy(value="service")
    public <T> void store(Class<T> service, T implementation) {
        if (Sortable.class.isAssignableFrom(service)) {
            storeSortable((Class<Sortable>) service, (Sortable)implementation);
            return;
        }
        synchronized (service) {
            Collection<Class<?>> implementations = classes.get(service);
            Collection<T> serviceInstances = (Collection<T>) instances.get(service);
            if (implementations == null) {
                implementations = new ArrayList<>();
                classes.put(service, implementations);
                serviceInstances = new ArrayList<>();
                instances.put(service, serviceInstances);
            }
            implementations.add(implementation.getClass());
            try {
                serviceInstances.add(implementation);
            } catch (Exception e) {
                throw new IllegalStateException("The service <" + implementation.getClass().getName() + "> of type <" + service.getName() + "> can't be stored.", e);
            }
        }
    }

    public void clear() {
        instances.clear();
        classes.clear();
    }

    @Override
    public <T> Collection<T> service(Class<T> serviceClass) {
        return load(serviceClass);
    }

    @GuardedBy(value="service")
    private <T extends Sortable> void storeSortable(Class<T> service, T implementation) {
        synchronized (service) {
            Collection<Class<?>> implementations = classes.get(service);
            Collection<T> serviceInstances = (Collection<T>) instances.get(service);
            if (implementations == null) {
                implementations = new ArrayList<>();
                classes.put(service, implementations);
                serviceInstances = new TreeSet<>(Sortable.COMPARATOR);
                instances.put(service, serviceInstances);
            }
            implementations.add(implementation.getClass());
            try {
                serviceInstances.add(implementation);
            } catch (Exception e) {
                throw new IllegalStateException("The service <" + implementation.getClass().getName() + "> of type <" + service.getName() + "> can't be stored.", e);
            }
        }
    }

}
