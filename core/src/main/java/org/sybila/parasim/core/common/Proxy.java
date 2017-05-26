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
package org.sybila.parasim.core.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import org.sybila.parasim.core.api.InjectionPoint;
import org.sybila.parasim.core.api.Resolver;
import org.sybila.parasim.core.impl.GenericInjectionPoint;

public class Proxy {

    private static final MethodFilter ALL_HANDLED = new MethodFilter() {
        @Override
        public boolean isHandled(Method method) {
            return true;
        }
    };

    private Proxy() {
    }

    public static final <T> T of(Class<T> clazz, MethodHandler handler) throws Exception {
        ProxyFactory proxyFactory = new ProxyFactory();
        if (clazz.isInterface()) {
            proxyFactory.setInterfaces(new Class<?>[]{clazz});
        } else {
            proxyFactory.setSuperclass(clazz);
        }
        proxyFactory.setFilter(ALL_HANDLED);
        return clazz.cast(proxyFactory.create(new Class<?>[0], new Object[0], handler));
    }

    public static final <T> T of(Class<T> interfaze, Class<? extends T> implementation, Resolver resolver) throws Exception {
        if (!interfaze.isInterface()) {
            throw new IllegalArgumentException("The 'interfaze' parameter has to be interface, " + interfaze.getName() + " given.");
        }
        if (implementation.getConstructors().length != 1) {
            throw new IllegalArgumentException("The implementation class " + implementation.getName() + " has to have exactly one constructor.");
        }
        Constructor<T> constructor = (Constructor<T>) implementation.getConstructors()[0];
        Object[] args = new Object[constructor.getParameterTypes().length];
        for (int i=0; i<constructor.getGenericParameterTypes().length; i++) {
            InjectionPoint injectionPoint = new GenericInjectionPoint(constructor.getParameterAnnotations()[i], constructor.getGenericParameterTypes()[i]);
            args[i] = resolver.resolve(ReflectionUtils.getType(injectionPoint.getType()), injectionPoint.getQualifier());
            if (injectionPoint.required() && args[i] == null) {
                throw new IllegalStateException("Can't resolve required constructor parameter " + ReflectionUtils.getType(injectionPoint.getType()).getName() + " with " + injectionPoint.getQualifier().getSimpleName() + " qualifier.");
            }
        }
        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
        }
        return constructor.newInstance(args);
    }

}
