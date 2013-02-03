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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sybila.parasim.core.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.core.annotation.Inject;
import org.sybila.parasim.core.annotation.Observes;
import org.sybila.parasim.core.annotation.Provide;
import org.sybila.parasim.core.api.Extension;
import org.sybila.parasim.core.api.InjectionPoint;
import org.sybila.parasim.core.api.ObservingPoint;
import org.sybila.parasim.core.api.ProvidingPoint;
import org.sybila.parasim.core.common.ReflectionUtils;

public class ExtensionImpl implements Extension {

    private final Class<? extends Annotation> scope;
    private final Class<?> targetClass;
    private final Collection<InjectionPoint> injectionPoints;
    private final Collection<ProvidingPoint> providingPoints;
    private final Collection<ObservingPoint> observingPoints;

    public ExtensionImpl(Class<? extends Annotation> scope, Class<?> targetClass) throws Exception {
        Validate.notNull(scope);
        Validate.notNull(targetClass);
        this.scope = ReflectionUtils.clean(scope);
        this.targetClass = targetClass;
        Object target = ReflectionUtils.createInstance(targetClass);
        // find injection points
        List<InjectionPoint> newInjectionPoints = new ArrayList<>();
        for (Field field: targetClass.getDeclaredFields()) {
            if (field.getAnnotation(Inject.class) != null) {
                newInjectionPoints.add(new InjectionField(field, targetClass));
            }
        }
        injectionPoints = Collections.unmodifiableCollection(newInjectionPoints);
        // find observing methods
        List<ObservingPoint> newObservingPoints = new ArrayList<>();
        for (Method method: targetClass.getDeclaredMethods()) {
            if (method.getParameterTypes().length > 0 && ReflectionUtils.loadAnnotation(method.getParameterAnnotations()[0], Observes.class) != null) {
                newObservingPoints.add(new ObservingMethod(target, method));
            }
        }
        observingPoints = Collections.unmodifiableCollection(newObservingPoints);
        // find providing points
        // -- fields
        List<ProvidingPoint> newProvidingPoints = new ArrayList<>();
        for (Field field: targetClass.getDeclaredFields()) {
            if (field.getAnnotation(Provide.class) != null) {
                newProvidingPoints.add(new ProvidingField(field, target));
            }
        }
        // -- methods
        for (Method method: targetClass.getDeclaredMethods()) {
            if (method.getAnnotation(Provide.class) != null) {
                newProvidingPoints.add(new ProvidingMethod(method, target));
            }
        }
        providingPoints = Collections.unmodifiableCollection(newProvidingPoints);
    }

    @Override
    public Class<? extends Annotation> scope() {
        return scope;
    }

    @Override
    public Collection<InjectionPoint> getInjections() {
        return injectionPoints;
    }

    @Override
    public Collection<ObservingPoint> getObservers() {
        return observingPoints;
    }

    @Override
    public Collection<ProvidingPoint> getProviders() {
        return providingPoints;
    }

    @Override
    public String toString() {
        return targetClass.getName();
    }

}
