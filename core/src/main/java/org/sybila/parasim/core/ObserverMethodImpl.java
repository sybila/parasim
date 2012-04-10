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
import org.sybila.parasim.core.context.Context;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ObserverMethodImpl implements ObserverMethod{

    private Context context;
    private Method method;
    private Object target;

    public ObserverMethodImpl(Object target, Context context, Method method) {
        if (target == null) {
            throw new IllegalArgumentException("The parameter [target] is null.");
        }
        if (context == null) {
            throw new IllegalArgumentException("The parameter [context] is null.");
        }
        if (method == null) {
            throw new IllegalArgumentException("The parameter [method] is null.");
        }
        this.context = context;
        this.target = target;
        this.method = method;
    }

    public void invoke(Manager manager, Object event) {
        // resolve parameters
        Object[] parameters = new Object[method.getParameterTypes().length];
        parameters[0] = event;
        for(int i=1; i<parameters.length; i++) {
            parameters[i] = manager.resolve(method.getParameterTypes()[i], context);
            if (parameters[i] == null) {
                throw new InvocationException("There is no available instance for class <" + method.getParameterTypes()[i].getName() + ">.");
            }
        }
        try {
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            method.invoke(target, parameters);
        } catch(Exception e) {
            e.printStackTrace();
            throw new InvocationException(e);
        }
    }

    public Method getMethod() {
        return method;
    }

    public Type getType() {
        return method.getGenericParameterTypes()[0];
    }
}
