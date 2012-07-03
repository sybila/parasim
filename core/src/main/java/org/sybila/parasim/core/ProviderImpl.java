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
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import org.sybila.parasim.core.context.Context;
import org.sybila.parasim.core.extension.interceptor.api.InterceptorRegistry;
import org.sybila.parasim.core.extension.interceptor.api.Managed;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ProviderImpl<I> implements Provider<I> {

    private final Class<? extends Annotation> qualifier;
    private final I value;
    private static final MethodFilter ALL_HANDLED = new MethodFilter() {
        @Override
        public boolean isHandled(Method method) {
            return true;
        }
    };

    private ProviderImpl(final ProvidingPoint providingPoint, Class<I> type, Class<? extends Annotation> qualifier, Manager manager) {
        if (providingPoint == null) {
            throw new IllegalArgumentException("The parameter [providingPoint] is null.");
        }
        if (type == null) {
            throw new IllegalArgumentException("The parameter [type] is null.");
        }
        if (type.isPrimitive()) {
            throw new IllegalArgumentException("The primitive type can't be provided.");
        }
        if (qualifier == null) {
            throw new IllegalArgumentException("The parameter [qualifier] is null.");
        }
        if (manager == null) {
            throw new IllegalArgumentException("The parameter [manager] is null.");
        }
        this.qualifier = qualifier;
        ProxyFactory proxyFactory = new ProxyFactory();
        if (type.isInterface()) {
            proxyFactory.setInterfaces(new Class<?>[] { type });
        } else {
            proxyFactory.setSuperclass(type);
        }
        proxyFactory.setFilter(ALL_HANDLED);
        try {
            I proxyObject = type.cast(proxyFactory.create(new Class<?>[0], new Object[0], new MethodHandler() {
                private Object toDelegate;
                @Override
                public Object invoke(Object target, Method thisMethod, Method proceed, Object[] args) throws Throwable {
                    if (providingPoint.fresh()) {
                            if (!thisMethod.isAccessible()) {
                                thisMethod.setAccessible(true);
                            }
                            return thisMethod.invoke(providingPoint.value(), args);
                        } else {
                            if (toDelegate == null) {
                                toDelegate = providingPoint.value();
                            }
                            if (!thisMethod.isAccessible()) {
                                thisMethod.setAccessible(true);
                            }
                            return thisMethod.invoke(toDelegate, args);
                        }
                }
            }));
            InterceptorRegistry interceptorRegistry = manager.resolve(InterceptorRegistry.class, Managed.class, manager.getRootContext());
            if (interceptorRegistry != null) {
                this.value = interceptorRegistry.intercepted(proxyObject).getProxyObject();
            } else {
                this.value = proxyObject;
            }

        } catch (Exception e) {
            throw new IllegalStateException("The proxy for ["+type.getName()+"] can't be created", e);
        }
    }

    public static <I> Provider<I> of(ProvidingPoint providingPoint, Class<I> type, Class<? extends Annotation> qualifier, Manager manager) {
        return new ProviderImpl<I>(providingPoint, type, qualifier, manager);
    }

    public static <I> void bind(ManagerImpl manager, Context context, ProvidingPoint providingPoint, Class<I> type, Class<? extends Annotation> qualifier) {
        if (manager == null) {
            throw new IllegalArgumentException("The parameter [manager] is null.");
        }
        if (context == null) {
            throw new IllegalArgumentException("The parameter [context] is null.");
        }
        manager.bind(type, qualifier, context, of(providingPoint, type, qualifier, manager).get());
    }

    public I get() {
        return value;
    }

    public Class<? extends Annotation> qualifier() {
        return qualifier;
    }

}
