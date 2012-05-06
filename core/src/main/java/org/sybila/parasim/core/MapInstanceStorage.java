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

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.sybila.parasim.core.annotations.Any;
import org.sybila.parasim.core.annotations.Empty;
import org.sybila.parasim.core.annotations.Qualifier;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class MapInstanceStorage implements InstanceStorage {

    private Map<Class<?>, Map<Class<? extends Annotation>, Object>> instances;

    public MapInstanceStorage(Map<Class<?>, Map<Class<? extends Annotation>,Object>> instances) {
        if (instances == null) {
            throw new IllegalArgumentException("The parameter [instances] is null.");
        }
        this.instances = instances;
    }

    public MapInstanceStorage() {
        this(new ConcurrentHashMap<Class<?>, Map<Class<? extends Annotation>, Object>>());
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

    public InstanceStorage clear() {
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

        throw new IllegalArgumentException("There is no available instance of <" + type.getName() + "> for qualifier <" + (Proxy.isProxyClass(qualifier) ? qualifier.getInterfaces()[0].getSimpleName() : qualifier.getSimpleName()) + ">.");
    }

}