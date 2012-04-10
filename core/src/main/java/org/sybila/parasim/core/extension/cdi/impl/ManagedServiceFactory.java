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
package org.sybila.parasim.core.extension.cdi.impl;

import org.sybila.parasim.core.ManagerImpl;
import org.sybila.parasim.core.context.Context;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ManagedServiceFactory extends AbstractServiceFactory {

    private ManagerImpl manager;

    public ManagedServiceFactory(ManagerImpl manager) {
        if (manager == null) {
            throw new IllegalArgumentException("The parameter [manager] is null.");
        }
        this.manager = manager;
    }

    public <T> T getService(Class<T> type, Context context) {
        return manager.resolve(type, context);
    }

    public boolean isServiceAvailable(Class<?> type, Context context) {
        return manager.resolve(type, context) != null;
    }

    @Override
    protected <T> void bind(Class<T> clazz, Context context, Object value) {
        manager.bind(clazz, context, (T) value);
    }
}
