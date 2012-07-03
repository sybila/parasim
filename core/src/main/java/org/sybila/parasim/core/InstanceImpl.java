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
import java.lang.reflect.Method;
import javassist.util.proxy.MethodFilter;
import org.sybila.parasim.core.context.Context;
import org.sybila.parasim.core.extension.interceptor.api.InterceptionException;
import org.sybila.parasim.core.extension.interceptor.api.InterceptorRegistry;
import org.sybila.parasim.core.extension.interceptor.api.Managed;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class InstanceImpl<T> implements Instance<T> {

    private final ManagerImpl manager;
    private final Context context;
    private final Class<T> type;
    private final Class<? extends Annotation> qualifier;
    private static final MethodFilter ALL_HANDLED = new MethodFilter() {
        @Override
        public boolean isHandled(Method method) {
            return true;
        }
    };

    private InstanceImpl(Class<T> type, Class<? extends Annotation> qualifier, Context context, ManagerImpl manager) {
        if (type == null) {
            throw new IllegalArgumentException("The parameter [type] is null.");
        }
        if (manager == null) {
            throw new IllegalArgumentException("The parameter [manager] is null.");
        }
        if (context == null) {
            throw new IllegalArgumentException("The parameter [context] is null.");
        }
        if (qualifier == null) {
            throw new IllegalArgumentException("The parameter [qualifier] is null.");
        }
        this.manager = manager;
        this.type = type;
        this.context = context;
        this.qualifier = qualifier;
    }

    public static <T> Instance<T> of(Class<T> type, Class<? extends Annotation> qualifier, Context context, ManagerImpl manager) {
        return new InstanceImpl<T>(type, qualifier, context, manager);
    }

    public T get() {
        return manager.resolve(type, qualifier, context);
    }

    public void set(T instance) {
        try {
            InterceptorRegistry interceptorRegistry = manager.resolve(InterceptorRegistry.class, Managed.class, manager.getRootContext());
            if (interceptorRegistry != null && interceptorRegistry.canBeIntercepted(instance)) {
                manager.bind(type, qualifier, context, interceptorRegistry.intercepted(instance).getProxyObject());
            } else {
                manager.bind(type, qualifier, context, instance);
            }
        } catch (InterceptionException e) {
            throw new IllegalStateException("Can't set the instance.", e);
        }
    }

    public Class<? extends Annotation> qualifier() {
        return qualifier;
    }

}
