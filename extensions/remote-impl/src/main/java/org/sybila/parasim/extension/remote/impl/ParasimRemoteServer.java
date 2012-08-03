package org.sybila.parasim.extension.remote.impl;

import java.rmi.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.ManagerImpl;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.extension.remote.api.RemoteHostActivity;
import org.sybila.parasim.extension.remote.api.RemoteHostControl;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ParasimRemoteServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParasimRemoteServer.class);

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            throw new IllegalArgumentException("The server has to be executed with an argument representing a target host for RMI.");
        }
        Manager manager = null;
        try {
            manager = ManagerImpl.create();
            manager.start();
            manager.resolve(Registry.class, Default.class, manager.getRootContext()).bind(RemoteHostControl.REMOTE_MANAGER_CONTEXT, new RemoteManagerImpl(manager));
            manager.resolve(RemoteHostActivity.class, Default.class, manager.getRootContext()).waitUntilFinished();
        } finally {
            if (manager != null) {
                try {
                    manager.shutdown();
                } catch(Exception e) {
                    LOGGER.warn("Can't shutdown the manager.", e);
                }
            }
        }

    }

}
