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
package org.sybila.parasim.core.extension.lifecycle.impl;

import java.lang.annotation.Annotation;
import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.ManagerImpl;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.core.context.Context;
import org.sybila.parasim.core.extension.lifecycle.api.InstanceManager;
import org.sybila.parasim.core.spi.DelegatedResolver;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class LifecycleResolver implements DelegatedResolver {

    @Override
    public int getPrecedence() {
        return 0;
    }

    @Override
    public <T> T resolve(Manager manager, Class<T> type, Class<? extends Annotation> qualifier, Context context) {
        if (type.isAssignableFrom(InstanceManager.class)) {
            return null;
        }
        InstanceManager instanceManager = manager.resolve(InstanceManager.class, Default.class, context);
        if (instanceManager == null) {
            return null;
        } else {
            T result = instanceManager.getInstance(type, qualifier, context);
            if (result != null) {
                ((ManagerImpl) manager).bind(type, qualifier, context, result);
            }
            return result;
        }
    }

}
