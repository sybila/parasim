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

import java.rmi.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.ManagerImpl;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.extension.remote.api.RemoteHostActivity;
import org.sybila.parasim.extension.remote.api.RemoteManager;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ParasimRemoteServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParasimRemoteServer.class);

    public static void main(String[] args) throws Exception {
        start();
    }

    public static void start() throws Exception {
        Manager manager = ManagerImpl.create();
        manager.start();
        manager.resolve(Registry.class, Default.class, manager.getRootContext()).bind(Default.class.getSimpleName() + "-" + RemoteManager.class.getName(), new RemoteManagerImpl(manager));
        manager.resolve(RemoteHostActivity.class, Default.class, manager.getRootContext()).waitUntilFinished();
    }

}
