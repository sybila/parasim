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
package org.sybila.parasim.core.impl;

import java.io.Serializable;
import java.lang.reflect.Method;
import javassist.util.proxy.MethodHandler;
import org.sybila.parasim.core.api.ProvidingPoint;
import org.sybila.parasim.core.api.Resolver;

public class ProvidingMethodHandler implements MethodHandler, Serializable {

    private final ProvidingPoint providingPoint;
    private final Resolver resolver;
    private Object toDelegate;

    public ProvidingMethodHandler(Resolver resolver, ProvidingPoint providingPoint) {
        this.providingPoint = providingPoint;
        this.resolver = resolver;
    }

    @Override
    public Object invoke(Object target, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        if (toDelegate == null) {
            toDelegate = providingPoint.value(resolver);
        }
        if (!thisMethod.isAccessible()) {
            thisMethod.setAccessible(true);
        }
        return thisMethod.invoke(toDelegate, args);
    }
}
