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

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.core.annotation.Inject;
import org.sybila.parasim.core.api.InjectionPoint;
import org.sybila.parasim.core.common.ReflectionUtils;

public class GenericInjectionPoint implements InjectionPoint, Serializable {

    private final Annotation[] annotations;
    private final Type type;
    private final boolean required;
    private final Class<? extends Annotation> qualifier;

    public GenericInjectionPoint(Annotation[] annotations, Type type) {
        Validate.notNull(annotations);
        Validate.notNull(type);
        this.annotations = annotations;
        this.type = type;
        this.qualifier = ReflectionUtils.loadQualifier(annotations);
        Inject found = ReflectionUtils.loadAnnotation(annotations, Inject.class);
        if (found == null) {
            required = true;
        } else {
            required = found.required();
        }
    }

    @Override
    public boolean required() {
        return required;
    }

    @Override
    public <T> void set(T value) {
        throw new UnsupportedOperationException("Generic injection point can't set value.");
    }

    @Override
    public Class<? extends Annotation> getQualifier() {
        return qualifier;
    }

    @Override
    public Type getType() {
        return type;
    }

}
