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
package org.sybila.parasim.extension.remote.impl;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.extension.remote.api.RemoteManager;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class RemoteManagerImpl extends UnicastRemoteObject implements RemoteManager {

    private final Manager manager;

    public RemoteManagerImpl(Manager manager) throws RemoteException {
        Validate.notNull(manager);
        this.manager = manager;
    }

    @Override
    public <T extends Serializable> Collection<T> service(Class<T> serviceClass) throws RemoteException {
        return manager.service(serviceClass);
    }

    @Override
    public <T extends Serializable> T resolve(Class<T> type, Class<? extends Annotation> qualifier) throws RemoteException {
        return manager.resolve(type, qualifier, manager.getRootContext());
    }

    @Override
    public void forceLoad(Class<? extends Remote> type, Class<? extends Annotation> qualifier) throws RemoteException {
        String name = qualifier.getSimpleName() + "-" + type.getName();
        if (!new HashSet<>(Arrays.asList(manager.resolve(Registry.class, Default.class, manager.getRootContext()).list())).contains(name)) {
            try {
                manager.resolve(Registry.class, Default.class, manager.getRootContext()).bind(name, manager.resolve(type, qualifier, manager.getRootContext()));
            } catch (Exception e) {
                throw new IllegalStateException("Can't bind " + name + ".", e);
            }
        };
        manager.resolve(type, qualifier, manager.getRootContext());
    }

}
