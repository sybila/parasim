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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.annotations.Observes;
import org.sybila.parasim.core.annotations.Provide;
import org.sybila.parasim.core.context.Context;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ExtensionImpl implements Extension {

    private Context context;
    private Collection<ContextEventPoint> contextEventPoints;
    private Collection<EventPoint> eventPoints;
    private Object target;
    private Collection<InjectionPoint> injectionPoints;
    private Collection<ObserverMethod> observers;
    private Collection<ProvidingPoint> providingPoints;

    public ExtensionImpl(Object target, Context context) {
        if (target == null) {
            throw new IllegalArgumentException("The parameter [target] is null.");
        }
        if (context == null) {
            throw new IllegalArgumentException("The parameter [context] is null.");
        }

        this.target = target;
        this.context = context;

        this.contextEventPoints = new ArrayList<ContextEventPoint>();
        this.injectionPoints = new ArrayList<InjectionPoint>();
        this.eventPoints = new ArrayList<EventPoint>();
        this.observers = new ArrayList<ObserverMethod>();
        this.providingPoints = new ArrayList<ProvidingPoint>();

        // find event and injection points
        for (Field field: target.getClass().getDeclaredFields()) {
            if (isAnnotationPresent(Inject.class, field.getDeclaredAnnotations())) {
                if (field.getType() == Instance.class) {
                    injectionPoints.add(new InjectionPointImpl(target, field));
                } else if (field.getType() == Event.class) {
                    eventPoints.add(new EventPointImpl(target, field));
                } else if (field.getType() == ContextEvent.class) {
                    contextEventPoints.add(new ContextEventPointImpl(target, field));
                }
            }
            if (isAnnotationPresent(Provide.class, field.getDeclaredAnnotations())) {
                providingPoints.add(new ProvidingFieldPoint(target, field));
            }
        }
        // find observers
        for (Method method: target.getClass().getDeclaredMethods()) {
            if (isAnnotationPresent(Provide.class, method.getAnnotations())) {
                providingPoints.add(new ProvidingMethodPoint(target, method, context, method.getAnnotation(Provide.class).fresh()));
            }
            if (method.getParameterTypes().length == 0 || method.getParameterAnnotations().length == 0) {
                continue;
            }
            if (isAnnotationPresent(Observes.class, method.getParameterAnnotations()[0])) {
                observers.add(new ObserverMethodImpl(target, context, method));
            }
        }
    }

    public Context getContext() {
        return context;
    }

    public Collection<ContextEventPoint> getContextEventPoints() {
        return Collections.unmodifiableCollection(contextEventPoints);
    }

    public Collection<EventPoint> getEventPoints() {
        return Collections.unmodifiableCollection(eventPoints);
    }

    public Collection<InjectionPoint> getInjectionPoints() {
        return Collections.unmodifiableCollection(injectionPoints);
    }

    public Collection<ObserverMethod> getObservers() {
        return Collections.unmodifiableCollection(observers);
    }

    public Collection<ProvidingPoint> getProvidingPoints() {
        return providingPoints;
    }

    private boolean isAnnotationPresent(Class<? extends Annotation> needle, Annotation[] haystack) {
        for (Annotation a: haystack) {
            if (a.annotationType() == needle) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return target.getClass().getCanonicalName();
    }
}