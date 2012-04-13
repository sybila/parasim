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

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.core.context.Context;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ObserverMethodImpl extends AbstractTyped implements ObserverMethod{

    private final Context context;
    private final Method method;

    public ObserverMethodImpl(Object target, Context context, Method method) {
        super(target, Default.class);
        if (context == null) {
            throw new IllegalArgumentException("The parameter [context] is null.");
        }
        if (method == null) {
            throw new IllegalArgumentException("The parameter [method] is null.");
        }
        this.context = context;
        this.method = method;
    }

    public void invoke(Manager manager, Object event) {
        // resolve parameters
        Object[] parameters = new Object[getMethod().getParameterTypes().length];
        parameters[0] = event;
        for(int i=1; i<parameters.length; i++) {
            parameters[i] = manager.resolve(getMethod().getParameterTypes()[i], loadQualifier(getMethod().getParameterTypes()[i].getDeclaredAnnotations()), context);
            if (parameters[i] == null) {
                throw new InvocationException("There is no available instance for class <" + getMethod().getParameterTypes()[i].getName() + ">.");
            }
        }
        try {
            if (!getMethod().isAccessible()) {
                getMethod().setAccessible(true);
            }
            getMethod().invoke(getTarget(), parameters);
        } catch(Exception e) {
            e.printStackTrace();
            throw new InvocationException(e);
        }
    }

    @Override
    public Method getMethod() {
        return method;
    }

    public Type getType() {
        return method.getGenericParameterTypes()[0];
    }
}
