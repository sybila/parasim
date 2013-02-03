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
package org.sybila.parasim.core.impl.enrichment;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.sybila.parasim.core.annotation.Provide;
import org.sybila.parasim.core.api.Binder;
import org.sybila.parasim.core.api.Context;
import org.sybila.parasim.core.api.ProvidingPoint;
import org.sybila.parasim.core.common.Proxy;
import org.sybila.parasim.core.common.ReflectionUtils;
import org.sybila.parasim.core.impl.ProvidingField;
import org.sybila.parasim.core.impl.ProvidingMethod;
import org.sybila.parasim.core.impl.ProvidingMethodHandler;
import org.sybila.parasim.core.spi.enrichment.Enricher;

public class ProvidingEnricher implements Enricher<Object> {

    @Override
    public void enrich(Object target, Context context) {
        Binder binder = (Binder) context;
        for (Field field : target.getClass().getDeclaredFields()) {
            if (field.getAnnotation(Provide.class) != null) {
                ProvidingPoint providingPoint = new ProvidingField(field, target);
                provide(context, providingPoint);
            }
        }
        for (Method method: target.getClass().getDeclaredMethods()) {
            if (method.getAnnotation(Provide.class) != null) {
                ProvidingPoint providingPoint = new ProvidingMethod(method, target);
                provide(context, providingPoint);
            }
        }
    }

    @Override
    public void resolve(Method method, Object[] args, Context context) {
    }

    @Override
    public int getPrecedence() {
        return -10;
    }

    @Override
    public Class<Object> getType() {
        return Object.class;
    }

    protected void provide(Context context, ProvidingPoint providingPoint) {
        Binder binder = (Binder) context;
        Class<?> type = ReflectionUtils.getType(providingPoint.getType());
        if ((!type.isInterface() && Modifier.isFinal(type.getModifiers())) || providingPoint.immediately()) {
            Object value = providingPoint.value(context);
            if (value != null) {
                bindUnsafely(binder, type, providingPoint.getQualifier(), value);
            } else {
                if (providingPoint.required()) {
                    throw new IllegalStateException("The providing value of required providing point " + providingPoint + " is null.");
                }
            }
        } else {
            try {
                bindUnsafely(binder, type, providingPoint.getQualifier(), Proxy.of(type, new ProvidingMethodHandler(context, providingPoint)));
            } catch (Exception e) {
                throw new IllegalStateException("Can't bind " + type.getName() + " with " + providingPoint.getQualifier().getSimpleName() + " qualifier.", e);
            }
        }
    }

    protected <T> void bindUnsafely(Binder binder, Class<T> type, Class<? extends Annotation> qualifer, Object instance) {
        binder.bind(type, qualifer, type.cast(instance));
    }
}
