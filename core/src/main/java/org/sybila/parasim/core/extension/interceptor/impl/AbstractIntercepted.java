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
package org.sybila.parasim.core.extension.interceptor.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyObject;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.core.extension.interceptor.api.Intercepted;
import org.sybila.parasim.core.extension.interceptor.api.InterceptionException;
import org.sybila.parasim.core.extension.interceptor.api.Invocation;
import org.sybila.parasim.core.spi.Interceptor;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractIntercepted<T extends Object> implements Intercepted<T> {

    private Collection<Interceptor> allMethodsInterceptors = new HashSet<>();
    private MethodHandler methodHandlerFromProxy;
    private T proxyObject;
    private Map<Method, Invocation> someMethodsInterceptors = new HashMap<>();
    private T targetObject;
    protected static final Invocation DEFAULT_INVOCATION = new Invocation() {

        @Override
        public Object invoke(Object target, Method method, Object... args) throws Throwable {
            if (target != null && method != null && !Modifier.isAbstract(method.getModifiers()) && !Modifier.isInterface(method.getModifiers())) {
                method.setAccessible(true);
                return method.invoke(target, args);
            } else {
                throw new IllegalStateException("");
            }
        }
    };
    protected static final MethodFilter ALL_HANDLED = new MethodFilter() {

        @Override
        public boolean isHandled(Method method) {
            return true;
        }
    };

    public AbstractIntercepted(T targetObject) {
        Validate.notNull(targetObject, "The parameter [targetObject] can't be null.");
        this.targetObject = targetObject;
        if (targetObject instanceof ProxyObject) {
            methodHandlerFromProxy = ((ProxyObject) targetObject).getHandler();
        }
    }

    @Override
    public T getProxyObject() throws InterceptionException {
        if (proxyObject == null) {
            proxyObject = createProxyObject();
        }
        return proxyObject;
    }

    @Override
    public Intercepted<T> intercept(Interceptor interceptor, Class<? extends Annotation>... annotations) {
        List<Method> methods = new ArrayList<>();
        for (Method method : targetObject.getClass().getDeclaredMethods()) {
            for (Class<? extends Annotation> annotation : annotations) {
                if (method.isAnnotationPresent(annotation)) {
                    methods.add(method);
                    break;
                }
            }
        }
        Method[] toIntercept = new Method[methods.size()];
        methods.toArray(toIntercept);
        intercept(interceptor, toIntercept);
        return this;
    }

    @Override
    public Intercepted<T> intercept(final Interceptor interceptor, Method... methods) {
        Validate.notNull(interceptor);
        for (final Method method : methods) {
            final Invocation interceptedInvocation = getMethodInvocation(method);
            someMethodsInterceptors.put(method, new Invocation() {
                @Override
                public Object invoke(Object target, Method method, Object... args) throws Throwable {
                    return interceptor.intercept(target, method, args, interceptedInvocation);
                }
            });
        }
        return this;
    }

    @Override
    public Intercepted<T> intercept(Interceptor interceptor) {
        Validate.notNull(interceptor);
        allMethodsInterceptors.add(interceptor);
        return this;
    }

    abstract protected T createProxyObject() throws InterceptionException;

    protected Collection<Interceptor> getInterceptorsForAllMethods() {
        return Collections.unmodifiableCollection(allMethodsInterceptors);
    }

    protected Invocation getMethodInvocation(Method method) {
        Validate.notNull(method);
        Invocation invocation = someMethodsInterceptors.get(method);
        if (invocation == null) {
            if (targetObject instanceof ProxyObject) {
                invocation = new Invocation() {
                    @Override
                    public Object invoke(Object target, Method method, Object... args) throws Throwable {
                        return methodHandlerFromProxy.invoke(target, method, method, args);
                    }
                };
            } else {
                invocation = DEFAULT_INVOCATION;
            }
            someMethodsInterceptors.put(method, invocation);
        }
        return invocation;
    }

    protected T getTargetObject() {
        return targetObject;
    }
}
