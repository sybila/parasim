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
package org.sybila.parasim.core.impl.remote;

import java.lang.annotation.Annotation;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.core.annotation.Default;
import org.sybila.parasim.core.api.Resolver;
import org.sybila.parasim.core.api.remote.Loader;

/**
 * @author <a href="mailto:xpapous1@mail.muni.cz">Jan Papousek</a>
 */
public class LoaderImpl implements Loader {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoaderImpl.class);

    private final Resolver resolver;
    private final int port;

    // for proxies
    protected LoaderImpl() {
        this.resolver = null;
        this.port = -1;
    }

    public LoaderImpl(Resolver resolver, int port) {
        this.resolver = resolver;
        this.port = port;
    }

    @Override
    public void load(Class<? extends Remote> type, Class<? extends Annotation> qualifier) throws RemoteException {
        String name = nameFor(type, qualifier);
        if (!new HashSet<>(Arrays.asList(resolver.resolve(Registry.class, Default.class).list())).contains(name)) {
            try {
                Registry registry = LocateRegistry.getRegistry("localhost", port);
                registry.bind(name, UnicastRemoteObject.exportObject(resolver.resolve(type, qualifier)));
                LOGGER.info(name + " registered");
            } catch (Exception e) {
                UnicastRemoteObject.unexportObject(resolver.resolve(type, qualifier), true);
                throw new IllegalStateException("Can't bind " + name + ".", e);
            }
        }
    }

    public static String nameFor(Class<? extends Remote> type, Class<? extends Annotation> qualifier) {
        return qualifier.getSimpleName() + "-" + type.getName();
    }

}
