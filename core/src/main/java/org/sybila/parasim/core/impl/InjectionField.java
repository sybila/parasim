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
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.core.annotation.Inject;
import org.sybila.parasim.core.api.InjectionPoint;
import org.sybila.parasim.core.common.ReflectionUtils;

public class InjectionField implements InjectionPoint {

    private final Field field;
    private final Object target;
    private final Class<? extends Annotation> qualifier;
    private Inject inject;

    public InjectionField(Field field, Object target) {
        Validate.notNull(field);
        Validate.notNull(target);
        inject = field.getAnnotation(Inject.class);
        Validate.notNull(inject, "Field without " + Inject.class.getName() + " annotation can't be considered as injection point.");
        this.field = field;
        this.target = target;
        this.qualifier = ReflectionUtils.loadQualifier(field.getAnnotations());
    }

    @Override
    public boolean required() {
        return inject.required();
    }

    @Override
    public <T> void set(T value) {
        ReflectionUtils.setFieldValue(target, field, value);
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
    public String toString() {
        return target.getClass() + "#" + field.getName();
    }

}
