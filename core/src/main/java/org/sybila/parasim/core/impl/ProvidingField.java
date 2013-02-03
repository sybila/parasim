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
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.core.annotation.Provide;
import org.sybila.parasim.core.api.ProvidingPoint;
import org.sybila.parasim.core.api.Resolver;
import org.sybila.parasim.core.api.Typed;
import org.sybila.parasim.core.common.ReflectionUtils;

public class ProvidingField implements ProvidingPoint{

    private final Field field;
    private final Object target;
    private final Provide provide;
    private final Class<? extends Annotation> qualifier;

    public ProvidingField(Field field, Object target) {
        Validate.notNull(field);
        Validate.notNull(target);
        provide = field.getAnnotation(Provide.class);
        Validate.notNull(provide, "Field without " + Provide.class.getName() + " annotation can't be providing point.");
        this.field = field;
        this.target = target;
        this.qualifier = ReflectionUtils.loadQualifier(field.getAnnotations());
    }

    @Override
    public Object value(Resolver resolver) {
        return ReflectionUtils.getFieldValue(target, field);
    }

    @Override
    public Class<? extends Annotation> getQualifier() {
        return qualifier;
    }

    @Override
    public Type getType() {
        return field.getGenericType();
    }

    @Override
    public Collection<Typed> dependencies() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public boolean immediately() {
        return provide.immediately();
    }

    @Override
    public boolean required() {
        return provide.required();
    }

    @Override
    public String toString() {
        return target.getClass() + "#" + field.getName();
    }

}
