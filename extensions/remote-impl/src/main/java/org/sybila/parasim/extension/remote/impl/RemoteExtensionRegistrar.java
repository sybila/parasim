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

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.core.Instance;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.annotations.Observes;
import org.sybila.parasim.core.annotations.Provide;
import org.sybila.parasim.core.event.ManagerStarted;
import org.sybila.parasim.core.event.ManagerStopping;
import org.sybila.parasim.extension.remote.api.RemoteControl;
import org.sybila.parasim.extension.remote.api.RemoteHostActivity;
import org.sybila.parasim.extension.remote.api.RemoteHostControl;
import org.sybila.parasim.extension.remote.configuration.RemoteConfiguration;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class RemoteExtensionRegistrar {

    @Inject
    private Instance<RemoteHostActivity> activity;
    private Registry providedRegistry;

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteExtensionRegistrar.class);

    public void registerRemoteHostActivity(@Observes ManagerStarted event, RemoteConfiguration configuration) {
        activity.set(new RemoteHostActivityImpl(configuration.getTimeout(), configuration.getTimeUnit()));
    }

    @Provide
    public RemoteControl provideRemoteControl(RemoteConfiguration configuration) throws URISyntaxException {
        if (configuration.getHosts() == null) {
            throw new IllegalStateException("Hosts aren't defined, so the remote control can't be provided.");
        }
        // create remote host controls
        Collection<RemoteHostControl> hostControls = new ArrayList<>();
        for (String host: configuration.getHosts()) {
            String[] hostSplit = host.split("@");
            String username = configuration.getUsername();
            if (hostSplit.length == 2) {
                username = hostSplit[0];
            }
            hostSplit = hostSplit[hostSplit.length - 1].split(":");
            URI uri = new URI(hostSplit[0]);
            File target = configuration.getTarget();
            if (hostSplit.length == 2) {
                target = new File(hostSplit[1]);
            }
            if (username == null) {
                hostControls.add(new RemoteHostControlImpl(uri, target));
            } else {
                hostControls.add(new RemoteHostControlImpl(uri, username, target));
            }
        }
        RemoteControl control = new RemoteControlImpl(hostControls);
        control.start(configuration.getTimeout(), configuration.getTimeUnit());
        return control;
    }

    @Provide
    public Registry provideRegistry(RemoteConfiguration configuration) throws RemoteException {
        if (System.getSecurityManager() == null) {
            if (System.getProperty("java.security.policy") == null) {
                System.setProperty("java.security.policy", RemoteHostControlImpl.SECURITY_POLICY_PATH);
            }
        }
        providedRegistry = LocateRegistry.createRegistry(configuration.getPort());
        return providedRegistry;
    }

    public void cleanRegistry(@Observes ManagerStopping event) {
        if (providedRegistry != null) {
            try {
                UnicastRemoteObject.unexportObject(providedRegistry, true);
                LOGGER.debug("RMI registry closed.");
            } catch (RemoteException e) {
                LOGGER.warn("Can't close RMI registry.", e);
            }
        }
    }
}
