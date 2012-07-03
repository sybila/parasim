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
package org.sybila.parasim.core;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.jcip.annotations.GuardedBy;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ServiceStorageImpl implements ServiceStorage {

    private Map<Class<?>, List<?>> instances = new ConcurrentHashMap<>();
    private Map<Class<?>, List<Class<?>>> classes = new ConcurrentHashMap<>();

    @Override
    public <T> Collection<T> load(Class<T> service) throws ServiceStorageException {
        List<T> result = (List<T>) instances.get(service);
        if (result == null) {
            return new ArrayList<>();
        } else {
            return Collections.unmodifiableCollection(result);
        }
    }

    @Override
    @GuardedBy(value="service")
    public <T> void store(Class<T> service, Class<? extends T> implementation) throws ServiceStorageException {
        synchronized (service) {
            List<Class<?>> implementations = classes.get(service);
            List<T> serviceInstances = (List<T>) instances.get(service);
            if (implementations == null) {
                implementations = new ArrayList<>();
                classes.put(service, implementations);
                serviceInstances = new ArrayList<>();
                instances.put(service, serviceInstances);
            }
            implementations.add(implementation);
            try {
                serviceInstances.add(createInstance(implementation));
            } catch (Exception e) {
                throw new ServiceStorageException("The service <" + implementation.getName() + "> of type <" + service.getName() + "> can't be stored.", e);
            }
        }
    }

    private <T> T createInstance(Class<T> type) throws Exception {
        for (Constructor<?> constructor: type.getDeclaredConstructors()) {
            if (constructor.getParameterTypes().length == 0) {
               if (!constructor.isAccessible()) {
                   constructor.setAccessible(true);
               }
               return (T) constructor.newInstance();
            }
        }
        throw new InvocationException("There is no empty constructor in class " + type.getName());
    }

}
