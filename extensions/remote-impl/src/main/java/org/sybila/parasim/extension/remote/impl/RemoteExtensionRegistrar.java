package org.sybila.parasim.extension.remote.impl;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.core.Instance;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.annotations.Observes;
import org.sybila.parasim.core.annotations.Provide;
import org.sybila.parasim.core.event.ManagerStarted;
import org.sybila.parasim.core.event.ManagerStopping;
import org.sybila.parasim.extension.remote.api.RemoteHostActivity;
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
