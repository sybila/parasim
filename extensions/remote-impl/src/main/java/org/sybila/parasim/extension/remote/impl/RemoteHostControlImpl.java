/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the
 *
 * @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.extension.remote.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.extension.remote.api.RemoteHostControl;
import org.sybila.parasim.extension.remote.api.RemoteManager;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class RemoteHostControlImpl implements RemoteHostControl {

    private final URI host;
    private Process process;
    private RemoteManager remoteManager;
    private boolean running = false;
    private final File target;
    private final String username;
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteHostControlImpl.class);
    private static final String MAIN_CLASS = ParasimRemoteServer.class.getName();
    public static final String SECURITY_POLICY_PATH = RemoteControlImpl.class.getClassLoader().getResource("parasim.remote.security.policy").getFile();

    public RemoteHostControlImpl(URI host, File target) {
        Validate.notNull(host);
        Validate.notNull(target);
        this.host = host;
        this.target = target;
        this.username = null;
    }

    public RemoteHostControlImpl(URI host, String username, File target) {
        Validate.notNull(host);
        Validate.notNull(target);
        Validate.notNull(username);
        this.host = host;
        this.target = target;
        this.username = username;
    }

    @Override
    public URI getHost() {
        return host;
    }

    @Override
    public RemoteManager getManager() {
        if (remoteManager == null) {
            remoteManager = loadManager(false);
        }
        return remoteManager;
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
        if (!clazz.equals(RemoteManager.class)) {
            getManager().forceLoad(clazz, qualifier);
        }
        String name = qualifier.getSimpleName() + "-" + clazz.getName();
        try {
            return (T) LocateRegistry.getRegistry(host.toString()).lookup(name);
        } catch (Exception e) {
            throw new IOException("Can't lookup the service called <" + name + ">");
        }
    }

    @Override
    public void shutdown() {
        if (process == null) {
            throw new IllegalStateException("The host control is not running.");
        }
        process.destroy();
    }

    @Override
    public void start(long time, TimeUnit unit) throws IOException {
        if (isRunning(true)) {
            throw new IllegalStateException("The remote host control is already running.");
        }
        RemoteProcessBuilder builder;
        if (username == null) {
            builder = new RemoteProcessBuilder(host);
        } else {
            builder = new RemoteProcessBuilder(host, username);
        }
        builder = builder.command("java -cp", target.getAbsolutePath());
        Enumeration<?> propertyNames = System.getProperties().propertyNames();
        while (propertyNames.hasMoreElements()) {
            String propertyName = (String) propertyNames.nextElement();
            if (propertyName.startsWith("parasim")) {
                builder = builder.command("-D" + propertyName + "=" + System.getProperty(propertyName));
            }
        }
        process = builder.command(MAIN_CLASS).spawn();
        long toTimeout = System.currentTimeMillis() + unit.toMillis(time);
        while (!isRunning(true)) {
            if (System.currentTimeMillis() > toTimeout) {
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    System.err.println(line);
                }
                throw new IOException("Can't start remote host control for " + host);
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }
        }
        running = true;
    }

    protected RemoteManager loadManager(boolean ignoredFailure) {
        try {
            return lookup(RemoteManager.class, Default.class);
        } catch (IOException e) {
            if (!ignoredFailure) {
                LOGGER.warn("The remote manager is unreachable.", e);
            }
            return null;
        }
    }

    protected boolean ping() {
        return loadManager(true) != null;
    }
}
