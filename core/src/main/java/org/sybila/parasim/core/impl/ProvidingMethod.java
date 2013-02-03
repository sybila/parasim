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
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.core.annotation.Provide;
import org.sybila.parasim.core.api.InjectionPoint;
import org.sybila.parasim.core.api.ProvidingPoint;
import org.sybila.parasim.core.api.Resolver;
import org.sybila.parasim.core.api.Typed;
import org.sybila.parasim.core.common.ReflectionUtils;

public class ProvidingMethod implements ProvidingPoint {

    private final Method method;
    private final Object target;
    private final Provide provide;
    private final Class<? extends Annotation> qualifier;
    private final InjectionPoint[] injectionPoints;

    public ProvidingMethod(Method method, Object target) {
        Validate.notNull(method);
        Validate.notNull(target);
        this.provide = method.getAnnotation(Provide.class);
        Validate.notNull(provide, "Field without " + Provide.class.getName() + " annotation can't be providing point.");
        this.method = method;
        this.target = target;
        this.qualifier = ReflectionUtils.loadQualifier(method.getAnnotations());
        injectionPoints = new InjectionPoint[method.getGenericParameterTypes().length];
        for (int i=0; i<method.getGenericParameterTypes().length; i++) {
            injectionPoints[i] = new GenericInjectionPoint(method.getParameterAnnotations()[i], method.getGenericParameterTypes()[i]);
        }
    }

    @Override
    public Collection<Typed> dependencies() {
        return new ArrayList<Typed>(Arrays.asList(injectionPoints));
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
    public Object value(Resolver resolver) {
        Object[] args = new Object[method.getParameterTypes().length];
        for (int i=0; i<injectionPoints.length; i++) {
            args[i] = resolver.resolve(ReflectionUtils.getType(injectionPoints[i].getType()), injectionPoints[i].getQualifier());
            if (injectionPoints[i].required() && args[i] == null) {
                throw new IllegalStateException(
                        "Can't resolve required method parameter " +
                        ReflectionUtils.getType(injectionPoints[i].getType()).getName() +
                        " with " + injectionPoints[i].getQualifier().getSimpleName() +
                        " qualifier in method " + target.getClass().getName() + "#" + method.getName() + "()");
            }
        }
        return ReflectionUtils.invoke(target, method, args);
    }

    @Override
    public Class<? extends Annotation> getQualifier() {
        return qualifier;
    }

    @Override
    public Type getType() {
        return method.getGenericReturnType();
    }

    @Override
    public String toString() {
        return target.getClass() + "#" + method.getName() + "()";
    }

}
