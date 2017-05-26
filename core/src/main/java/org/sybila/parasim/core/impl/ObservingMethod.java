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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sybila.parasim.core.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.core.annotation.Default;
import org.sybila.parasim.core.annotation.Observes;
import org.sybila.parasim.core.api.InjectionPoint;
import org.sybila.parasim.core.api.ObservingPoint;
import org.sybila.parasim.core.api.Resolver;
import org.sybila.parasim.core.common.ReflectionUtils;

public class ObservingMethod implements ObservingPoint {

    private final Method method;
    private final Object target;
    private final InjectionPoint[] injectionPoints;

    public ObservingMethod(Object target, Method method) {
        Validate.notNull(method);
        Validate.notNull(target);
        if (method.getParameterTypes().length == 0) {
            throw new IllegalArgumentException("Method without parameters can't be observing point.");
        }
        Observes observes = ReflectionUtils.loadAnnotation(method.getParameterAnnotations()[0], Observes.class);
        Validate.notNull(observes, "Method where the first parameter isn't annotated by " + Observes.class.getName() + " annotation can't be observing point.");
        this.method = method;
        this.target = target;
        injectionPoints = new InjectionPoint[method.getGenericParameterTypes().length-1];
        for (int i=1; i<method.getGenericParameterTypes().length; i++) {
            injectionPoints[i-1] = new GenericInjectionPoint(method.getParameterAnnotations()[i], method.getGenericParameterTypes()[i]);
        }
    }

    @Override
    public void observe(Resolver resolver, Object event) {
        Object[] args = new Object[method.getParameterTypes().length];
        args[0] = event;
        for (int i=0; i<injectionPoints.length; i++) {
            args[i+1] = resolver.resolve(ReflectionUtils.getType(injectionPoints[i].getType()), injectionPoints[i].getQualifier());
            if (injectionPoints[i].required() && args[i+1] == null) {
                throw new IllegalStateException("Can't resolve required method parameter " + ReflectionUtils.getType(injectionPoints[i].getType()).getName() + " with " + injectionPoints[i].getQualifier().getSimpleName() + " qualifier.");
            }
        }
        ReflectionUtils.invoke(target, method, args);
    }

    @Override
    public Class<? extends Annotation> getQualifier() {
        return Default.class;
    }

    @Override
    public Type getType() {
        return method.getGenericParameterTypes()[0];
    }

    @Override
    public String toString() {
        return target.getClass() + "#" + method.getName() + "()";
    }

}
