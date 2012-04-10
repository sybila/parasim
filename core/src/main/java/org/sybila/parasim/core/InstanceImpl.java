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

import org.sybila.parasim.core.context.Context;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class InstanceImpl<T> implements Instance<T> {

    private ManagerImpl manager;
    private Context context;
    private Class<T> type;

    private InstanceImpl(Class<T> type, Context context, ManagerImpl manager) {
        if (type == null) {
            throw new IllegalArgumentException("The parameter [type] is null.");
        }
        if (manager == null) {
            throw new IllegalArgumentException("The parameter [manager] is null.");
        }
        if (context == null) {
            throw new IllegalArgumentException("The parameter [context] is null.");
        }
        this.manager = manager;
        this.type = type;
        this.context = context;
    }

    public static <T> Instance<T> of(Class<T> type, Context context, ManagerImpl manager) {
        return new InstanceImpl<T>(type, context, manager);
    }

    public T get() {
        return manager.resolve(type, context);
    }

    public void set(T instance) {
        manager.bind(type, context, instance);
    }

}
