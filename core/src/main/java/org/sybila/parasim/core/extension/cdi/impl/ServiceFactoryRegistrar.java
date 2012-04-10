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

import org.sybila.parasim.core.Instance;
import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.ManagerImpl;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.annotations.Observes;
import org.sybila.parasim.core.event.ManagerStarted;
import org.sybila.parasim.core.extension.cdi.api.ServiceFactory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ServiceFactoryRegistrar {

    @Inject
    private Instance<ServiceFactory> serviceFactory;
    @Inject
    private Instance<Manager> manager;

    public void register(@Observes ManagerStarted event) {
        if (!(manager.get() instanceof ManagerImpl)) {
            throw new IllegalStateException("The service factory can be created only with [" + ManagerImpl.class.getName() + "].");
        }
        serviceFactory.set(new ManagedServiceFactory((ManagerImpl) (manager.get())));
    }
}
