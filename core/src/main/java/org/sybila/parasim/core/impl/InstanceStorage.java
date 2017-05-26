/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.sybila.parasim.core.annotation.Any;
import org.sybila.parasim.core.annotation.Empty;
import org.sybila.parasim.core.annotation.Qualifier;
import org.sybila.parasim.core.api.Resolver;
import org.sybila.parasim.core.api.ServiceRepository;
import org.sybila.parasim.core.common.AmbigousException;
import org.sybila.parasim.core.spi.InstanceCleaner;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class InstanceStorage {

    private final Map<Class<?>, Map<Class<? extends Annotation>, Object>> instances;
    private final ServiceRepository serviceRepository;

    protected InstanceStorage(Map<Class<?>, Map<Class<? extends Annotation>,Object>> instances, ServiceRepository serviceRepository) {
        if (instances == null) {
            throw new IllegalArgumentException("The parameter [instances] is null.");
        }
        if (serviceRepository == null) {
            throw new IllegalArgumentException("The parameter [serviceRepository] is null.");
        }
        this.instances = instances;
        this.serviceRepository = serviceRepository;
    }

    public InstanceStorage(ServiceRepository serviceRepository) {
        this(new ConcurrentHashMap<Class<?>, Map<Class<? extends Annotation>, Object>>(), serviceRepository);
    }

    public <T> InstanceStorage add(Class<T> type, Class<? extends Annotation> qualifier, T value) {
        if (type == null) {
            throw new IllegalArgumentException("The parameter [type] is null.");
        }
        if (value == null) {
            throw new IllegalArgumentException("The parameter [value] is null.");
        }
        if (qualifier == null) {
            throw new IllegalArgumentException("The parameter [qualifier] is null.");
        }
        if (!instances.containsKey(type)) {
            instances.put(type, new HashMap<Class<? extends Annotation>, Object>());
        }
        instances.get(type).put(
            Proxy.isProxyClass(qualifier) ? (Class<? extends Annotation>) qualifier.getInterfaces()[0] : qualifier,
            value
        );
        return this;
    }

    public InstanceStorage clear(Resolver resolver) {
        Collection<InstanceCleaner> cleaners = serviceRepository.service(InstanceCleaner.class);
        for (Map<Class<? extends Annotation>, Object> map: instances.values()) {
            for (Object instance: map.values()) {
                for (InstanceCleaner cleaner: cleaners) {
                    cleaner.clean(resolver, instance);
                }
            }
        }
        instances.clear();
        return this;
    }

    public <T> T get(Class<T> type, Class<? extends Annotation> qualifier) {
        if (type == null) {
            throw new IllegalArgumentException("The parameter [type] is null.");
        }
        Map<Class<? extends Annotation>, Object> qualifiedInstances = instances.get(type);
        if (qualifiedInstances == null) {
            return null;
        }
        if (qualifier.equals(Any.class)) {
            if (qualifiedInstances.size() > 1) {
                throw new AmbigousException("There is more available instance of class <" + type.getName() + ">.");
            }
            for (Object o: qualifiedInstances.values()) {
                return type.cast(o);
            }
        } else {
            Class<? extends Annotation> toFind = qualifier;
            while (!toFind.equals(Empty.class)) {
                if (Proxy.isProxyClass(toFind)) {
                    toFind = (Class<? extends Annotation>) toFind.getInterfaces()[0];
                }
                Qualifier qualified = toFind.getAnnotation(Qualifier.class);
                if (qualified == null) {
                    throw new IllegalArgumentException("The qualifier " + toFind.getName() + " has to be annotated by <" + Qualifier.class.getName() + ">. Found annotations: " + Arrays.toString(toFind.getAnnotations()));
                }
                Object o = qualifiedInstances.get(toFind);
                if (o != null) {
                    return type.cast(o);
                } else {
                    toFind = qualified.parent();
                }
            }
        }
        return null;
    }

}