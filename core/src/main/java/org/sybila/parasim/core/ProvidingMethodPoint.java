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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.sybila.parasim.core.context.Context;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ProvidingMethodPoint extends AbstractTypedMethod implements ProvidingPoint {

    private final boolean fresh;
    private final Context context;

    public ProvidingMethodPoint(Object target, Method method, Context context, boolean fresh) {
        super(target, method);
        if (context == null) {
            throw new IllegalArgumentException("The paramater [context] is null.");
        }
        this.fresh = fresh;
        this.context = context;
    }

    public boolean fresh() {
        return fresh;
    }

    public Object value() {
        Object[] params = new Object[getMethod().getParameterTypes().length];
        for (int i=0; i<params.length; i++) {
            params[i] = context.resolve(getMethod().getParameterTypes()[i], loadQualifier(getMethod().getParameterAnnotations()[i]));
            if (params[i] == null) {
                throw new InvocationException("There is no available instance for class <" + getMethod().getParameterTypes()[i].getName() + ">.");
            }
        }
        try {
            if (!getMethod().isAccessible()) {
                getMethod().setAccessible(true);
            }
            return getMethod().invoke(getTarget(), params);
        } catch (IllegalAccessException ex) {
            throw new InvocationException(ex);
        } catch (IllegalArgumentException ex) {
            throw new InvocationException(ex);
        } catch (InvocationTargetException ex) {
            throw new InvocationException(ex);
        }
    }
}