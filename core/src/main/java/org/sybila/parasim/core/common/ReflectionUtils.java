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
package org.sybila.parasim.core.common;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import org.sybila.parasim.core.annotation.Default;
import org.sybila.parasim.core.annotation.Qualifier;

public final class ReflectionUtils {

    private ReflectionUtils() {
    }

    public static Class<? extends Annotation> clean(Class<? extends Annotation> annotation) {
        return (Class<? extends Annotation>) (Proxy.isProxyClass(annotation) ? annotation.getInterfaces()[0] : annotation);
    }

    public static Class<?> getType(Type type) {
        // type is not parametrized
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        }
        // type is parametrized
        if (type instanceof ParameterizedType) {
            return getType(((ParameterizedType) type).getActualTypeArguments()[0]);
        }
        // type is wildcard;
        if (type instanceof WildcardType) {
            for (Type wildType : ((WildcardType) type).getUpperBounds()) {
                Class<?> upperBoundsType = getType(wildType);
                if (upperBoundsType != null) {
                    return upperBoundsType;
                }
            }
            for (Type wildType : ((WildcardType) type).getLowerBounds()) {
                Class<?> lowerBoundsType = getType(wildType);
                if (lowerBoundsType != null) {
                    return lowerBoundsType;
                }
            }
        }
        // not success
        return null;
    }

    public static <T> T createInstance(Class<T> type) throws Exception {
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

    public static <A extends Annotation> A loadAnnotation(Annotation[] annotations, Class<A> annotationClass) {
        for (Annotation annotation: annotations) {
            if (annotationClass.isAssignableFrom(annotation.annotationType())) {
                return annotationClass.cast(annotation);
            }
        }
        return null;
    }

    public static Class<? extends Annotation> loadQualifier(Annotation[] annotations) {
        for (Annotation annotation: annotations) {
            if (annotation.annotationType().getAnnotation(Qualifier.class) != null) {
                return clean(annotation.getClass());
            }
        }
        return Default.class;
    }

    public static Object invoke(Object target, Method method, Object... args) {
        try {
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            return method.invoke(target, args);
        } catch (Exception e) {
           throw new InvocationException("Can't invoke method " + method.getName() + " declared in " + target.getClass().getName(), e);
        }
    }

    public static Object getFieldValue(Object target, Field field) {
        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            return field.get(target);
        } catch (Exception e) {
            throw new InvocationException("Can't get field value.", e);
        }
    }

    public static void setFieldValue(Object target, Field field, Object value) {
        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            field.set(target, value);
        } catch (Exception e) {
            throw new InvocationException("Can't set field value.", e);
        }
    }
}
