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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.apache.commons.lang3.ArrayUtils;
import org.sybila.parasim.core.context.Context;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ProviderImpl<I> implements Provider<I> {

    private final I value;

    private ProviderImpl(final ProvidingPoint providingPoint, Class<I> type) {
        if (providingPoint == null) {
            throw new IllegalArgumentException("The parameter [providingPoint] is null.");
        }
        if (type == null) {
            throw new IllegalArgumentException("The parameter [type] is null.");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("The type of the provider has to be an interface, <" + type.getName() + "> given.");
        }
        if (type.isPrimitive()) {
            throw new IllegalArgumentException("The primitive type can't be provided.");
        }
        this.value = type.cast(
            Proxy.newProxyInstance(
                type.getClassLoader(),
                ArrayUtils.addAll(new Class<?>[] {type}, type.getInterfaces()),
                new InvocationHandler() {

                    private Object toDelegate;

                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if (providingPoint.fresh()) {
                            if (!method.isAccessible()) {
                                method.setAccessible(true);
                            }
                            return method.invoke(providingPoint.value(), args);
                        } else {
                            if (toDelegate == null) {
                                toDelegate = providingPoint.value();
                            }
                            if (!method.isAccessible()) {
                                method.setAccessible(true);
                            }
                            return method.invoke(toDelegate, args);
                        }
                    }
                }
            )
        );
    }

    public static <I> Provider<I> of(ProvidingPoint providingPoint, Class<I> type) {
        return new ProviderImpl<I>(providingPoint, type);
    }

    public static <I> void bind(ManagerImpl manager, Context context, ProvidingPoint providingPoint, Class<I> type) {
        if (manager == null) {
            throw new IllegalArgumentException("The parameter [manager] is null.");
        }
        if (context == null) {
            throw new IllegalArgumentException("The parameter [context] is null.");
        }
        manager.bind(type, context, of(providingPoint, type).get());
    }

    public I get() {
        return value;
    }

}
