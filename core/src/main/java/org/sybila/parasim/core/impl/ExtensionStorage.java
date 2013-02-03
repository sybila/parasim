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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.sybila.parasim.core.annotation.Application;
import org.sybila.parasim.core.annotation.Scope;
import org.sybila.parasim.core.api.Extension;
import org.sybila.parasim.core.api.ExtensionRepository;
import org.sybila.parasim.core.common.ReflectionUtils;

public class ExtensionStorage implements ExtensionRepository {

    private final Map<Class<? extends Annotation>, Collection<Class<?>>> classes = new ConcurrentHashMap<>();

    public <T> void store(Class<T> clazz) {
        Class<? extends Annotation> scope = getScope(clazz);
        synchronized(scope) {
            Collection<Class<?>> scoped = classes.get(scope);
            if (scoped == null) {
                scoped = new ArrayList<>();
                classes.put(scope, scoped);
            }
            scoped.add(clazz);
        }
    }

    public Collection<Extension> load(Class<? extends Annotation> scope) {
        Collection<Extension> result = new ArrayList<>();
        Class<? extends Annotation> s = ReflectionUtils.clean(scope);
        synchronized(s) {
            Collection<Class<?>> scoped = classes.get(s) ;
            if (scoped == null) {
                scoped = Collections.EMPTY_LIST;
            }
            for (Class<?> clazz: scoped) {
                try {
                    result.add(new ExtensionImpl(scope, clazz));
                } catch (Exception e) {
                    throw new IllegalStateException("Can't create an instance.", e);
                }
            }
        }
        return result;
    }

    public void clear() {
        classes.clear();
    }

    private Class<? extends Annotation> getScope(Class<?> clazz) {
        for (Annotation annotation: clazz.getDeclaredAnnotations()) {
           Class<? extends Annotation> annotationClass = ReflectionUtils.clean(annotation.annotationType());
            if (annotationClass.getAnnotation(Scope.class) == null) {
                continue;
            }
            return annotationClass;
        }
        return Application.class;
    }

    @Override
    public Collection<Extension> extensions(Class<? extends Annotation> scope) {
        return load(scope);
    }

}
