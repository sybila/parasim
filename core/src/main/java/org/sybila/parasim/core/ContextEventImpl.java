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
public class ContextEventImpl<C extends Context> implements ContextEvent<C> {

    private Context context;
    private Manager manager;

    private ContextEventImpl(Manager manager, Context context) {
        if (manager == null) {
            throw new IllegalArgumentException("The parameter [manager] is null.");
        }
        if (context == null) {
            throw new IllegalArgumentException("The parameter [context] is null.");
        }
        this.manager = manager;
        this.context = context;
    }

    public static <T extends Context> ContextEvent<T> of(Class<T> contextClass, Manager manager, Context parentContext) {
        return new ContextEventImpl<T>(manager, parentContext);
    }

    public void initialize(C context) {
        context.setParent(this.context);
        manager.initializeContext(context);
    }

    public void finalize(C context) {
        context.setParent(this.context);
        manager.finalizeContext(context);
    }
}
