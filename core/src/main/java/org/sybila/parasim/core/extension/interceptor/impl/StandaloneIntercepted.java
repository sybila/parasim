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

import java.lang.reflect.Method;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import org.sybila.parasim.core.extension.interceptor.api.InterceptionException;
import org.sybila.parasim.core.spi.Interceptor;
import org.sybila.parasim.core.extension.interceptor.api.Invocation;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class StandaloneIntercepted<T extends Object> extends AbstractIntercepted<T> {


    public StandaloneIntercepted(T targetObject) {
        super(targetObject);
    }

    @Override
    protected T createProxyObject() throws InterceptionException {
        try {
            MethodHandler methodHandler = new MethodHandler() {
                @Override
                public Object invoke(final Object o, final Method thisMethod, final Method proceed, final Object[] args) throws Throwable {
                    Method toInvoke = thisMethod;
                    Invocation invocation = getMethodInvocation(toInvoke);
                    for (final Interceptor interceptor: getInterceptorsForAllMethods()) {
                        final Invocation interceptedInvocation = invocation;
                        invocation = new Invocation() {
                            @Override
                            public Object invoke(Object target, Method method, Object... args) throws Throwable {
                                return interceptor.intercept(target, method, args, interceptedInvocation);
                            }
                        };
                    }
                    return invocation.invoke(getTargetObject(), thisMethod, args);
                }
            };
            if (getTargetObject() instanceof ProxyObject) {
                ((ProxyObject) getTargetObject()).setHandler(methodHandler);
                return getTargetObject();
            } else {
                ProxyFactory proxyFactory = new ProxyFactory();
                proxyFactory.setSuperclass(getTargetObject().getClass());
                proxyFactory.setFilter(ALL_HANDLED);
                return (T) proxyFactory.create(new Class<?>[0], new Object[0], methodHandler);
            }

        } catch (Exception e) {
            throw new InterceptionException("Can't create a proxy object.", e);
        }
    }
}
