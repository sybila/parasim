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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.core.annotation.Default;
import org.sybila.parasim.core.api.remote.HostControl;
import org.sybila.parasim.core.api.remote.Loader;

/**
 * @author <a href="mailto:xpapous1@mail.muni.cz">Jan Papousek</a>
 */
public class HostControlImpl implements HostControl {

    public static final String SECURITY_POLICY_PATH = HostControlImpl.class.getClassLoader().getResource("parasim.remote.security.policy").getFile();

    private static final Logger LOGGER = LoggerFactory.getLogger(HostControlImpl.class);

    private final URI host;
    private boolean running = false;
    private Loader loader;
    private Process process;

    public HostControlImpl(URI host) {
        this.host = host;
    }

    @Override
    public URI getHost() {
        return host;
    }

    @Override
    public boolean isRunning(boolean ping) {
        if (ping) {
            running = ping();
        }
        return running;
    }

    @Override
    public <T extends Remote> T lookup(Class<T> clazz, Class<? extends Annotation> qualifier) throws IOException {
        if (!clazz.equals(Loader.class)) {
            getLoader().load(clazz, qualifier);
        }
        String name = LoaderImpl.nameFor(clazz, qualifier);
        try {
            return (T) LocateRegistry.getRegistry(host.toString()).lookup(name);
        } catch (RemoteException | NotBoundException e) {
            throw new IOException("Can't lookup the service called <" + name + ">", e);
        }
    }

    protected Loader getLoader() {
        if (loader == null) {
            loader = loadLoader(false);
        }
        return loader;
    }

    protected Loader loadLoader(boolean ignoredFailure) {
        try {
            return lookup(Loader.class, Default.class);
        } catch (IOException e) {
            if (!ignoredFailure) {
                LOGGER.warn("The remote loader is unreachable.", e);
            }
            return null;
        }
    }

    protected boolean ping() {
        return loadLoader(true) != null;
    }

}
