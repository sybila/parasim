/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sybila.parasim.application.actions;

import org.sybila.parasim.application.ParasimOptions;
import org.sybila.parasim.core.annotation.Default;
import org.sybila.parasim.core.api.Manager;
import org.sybila.parasim.core.api.remote.Loader;

/**
 *
 * @author jpapouse
 */
public class StartServer extends AbstractAction<Void> {

    public StartServer(Manager manager, ParasimOptions options) {
        super(manager, options);
    }

    @Override
    public boolean isEnabled() {
        return getOptions().isServer();
    }

    @Override
    public Void call() throws Exception {
        getManager().resolve(Loader.class, Default.class).load(Loader.class, Default.class);
        LOGGER.info("server started");
        synchronized (StartServer.class) {
            StartServer.class.wait();
        }
        return null;
    }
}
