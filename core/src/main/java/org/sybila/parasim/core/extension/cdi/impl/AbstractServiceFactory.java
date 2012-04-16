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
package org.sybila.parasim.core.extension.cdi.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import org.sybila.parasim.core.ProviderImpl;
import org.sybila.parasim.core.ProvidingFieldPoint;
import org.sybila.parasim.core.ProvidingMethodPoint;
import org.sybila.parasim.core.ProvidingPoint;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.annotations.Provide;
import org.sybila.parasim.core.annotations.Qualifier;
import org.sybila.parasim.core.context.Context;
import org.sybila.parasim.core.extension.cdi.api.ServiceFactory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractServiceFactory implements ServiceFactory {

    @Override
    public void injectFields(Object target, Context context) {
        if (target == null) {
            throw new IllegalArgumentException("The parameter [target] is null.");
        }
        if (context == null) {
            throw new IllegalArgumentException("The parameter [context] is null.");
        }
        for (Field field : target.getClass().getDeclaredFields()) {
            if (field.getAnnotation(Inject.class) != null) {
               injectField(target, field, context);
            }
        }
    }

    @Override
    public void provideFieldsAndMethods(Object target, Context context) {
        if (target == null) {
            throw new IllegalArgumentException("The parameter [target] is null.");
        }
        if (context == null) {
            throw new IllegalArgumentException("The parameter [context] is null.");
        }
        for (Field field: target.getClass().getDeclaredFields()) {
            if (field.getAnnotation(Provide.class) != null) {
                ProvidingPoint providingPoint = new ProvidingFieldPoint(target, field);
                Class<?> type = getType(providingPoint.getType());
                bind(type, providingPoint.getQualifier(), context, providingPoint.value());
            }
        }
        for (Method method: target.getClass().getDeclaredMethods()) {
            if (method.getAnnotation(Provide.class) != null) {
                ProvidingPoint providingPoint = new ProvidingMethodPoint(target, method, context, method.getAnnotation(Provide.class).fresh());
                Class<?> type = getType(providingPoint.getType());
                bind(type, providingPoint.getQualifier(), context, ProviderImpl.of(providingPoint, type, providingPoint.getQualifier()).get());
            }
        }
    }

    abstract protected <T> void bind(Class<T> clazz, Class<? extends Annotation> qualifier, Context context, Object value);

    private static Class<?> getType(Type type) {
        // type is not parametrized
        if (type instanceof  Class<?>) {
            return (Class<?>) type;
        }
        // type is parametrized
        if (type instanceof ParameterizedType) {
            return getType(((ParameterizedType) type).getActualTypeArguments()[0]);
        }
        // type is wildcard;
        if (type instanceof WildcardType) {
            for (Type wildType: ((WildcardType) type).getUpperBounds()) {
                Class<?> upperBoundsType = getType(wildType);
                if (upperBoundsType != null) {
                    return upperBoundsType;
                }
            }
            for (Type wildType: ((WildcardType) type).getLowerBounds()) {
                Class<?> lowerBoundsType = getType(wildType);
                if (lowerBoundsType != null) {
                    return lowerBoundsType;
                }
            }
        }
        // not success
        return null;
    }

    private void injectField(Object object, Field field, Context context) {
        Class<? extends Annotation> qualifier = loadQualifier(field.getDeclaredAnnotations());
        Object service = null;
        if (qualifier != null) {
            service = getService(field.getType(), context, qualifier);
        } else {
            service = getService(field.getType(), context);
        }
        if (service == null) {
            throw new IllegalStateException("The service " + field.getType().getName() + " requested in " + object.getClass().getName() + " is not available.");
        }
        field.setAccessible(true);
        try {
            field.set(object, field.getType().cast(service));
        } catch (Exception ex) {
            throw new IllegalStateException("The service can't be injected.", ex);
        }
    }

    private Class<? extends Annotation> loadQualifier(Annotation[] annotations) {
        for (Annotation annotation: annotations) {
            if (annotation.annotationType().getAnnotation(Qualifier.class) != null) {
                return annotation.getClass();
            }
        }
        return null;
    }
}
