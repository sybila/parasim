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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.core.annotations.Qualifier;

public abstract class AbstractTyped implements Typed {

    private final Object target;
    private final Class<? extends Annotation> qualifier;

    public AbstractTyped(Object target, Class<? extends Annotation> qualifier) {
        if (target == null) {
            throw new IllegalArgumentException("The parameter [target] is null.");
        }
        if (qualifier == null) {
            throw new IllegalArgumentException("The parameter [qualifier] is null.");
        }
        this.target = target;
        this.qualifier = qualifier;
    }

    public Class<? extends Annotation> getQualifier() {
        return qualifier;
    }

    protected Object getTarget() {
        return target;
    }

    protected Type loadType(Type type) {
        if (!(type instanceof ParameterizedType)) {
            return type;
        }
        ParameterizedType loadedType = (ParameterizedType) type;
        if(loadedType.getActualTypeArguments()[0] instanceof ParameterizedType) {
            ParameterizedType first = (ParameterizedType)loadedType.getActualTypeArguments()[0];
            return (Class<?>)first.getRawType();
        } else {
            return (Class<?>)loadedType.getActualTypeArguments()[0];
        }
    }

    protected static Class<? extends Annotation> loadQualifier(Annotation[] annotations) {
        for (Annotation annotation: annotations) {
            if (annotation.getClass().getAnnotation(Qualifier.class) != null) {
                return annotation.getClass();
            }
        }
        return Default.class;
    }
}
