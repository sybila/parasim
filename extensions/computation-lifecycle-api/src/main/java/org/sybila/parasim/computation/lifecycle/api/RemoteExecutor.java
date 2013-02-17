/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.computation.lifecycle.api;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

/**
 * The entry point for {@link DistributedMemoryExecutor} to compute
 * computation instances on other nodes. The {@link DistributedMemoryExecutor}
 * is responsible for starting and destroying the computation.
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface RemoteExecutor extends Remote {

    /**
     * Destroys the computation with the given computation ID.
     * @param id ID of the computation
     * @throws RemoteException
     */
    void destroyComputation(UUID id) throws RemoteException;

    /**
     * @return ID of the node
     * @throws RemoteException
     */
    UUID getId() throws RemoteException;

    /**
     * The queue is an entry point for emitting and rescheduling the computation
     * instances.
     *
     * @param computation computation ID
     * @return queue for the given computation
     * @throws RemoteException
     * @see #startComputation(org.sybila.parasim.computation.lifecycle.api.RemoteMutableStatus, java.util.UUID)
     * @see #destroyComputation(java.util.UUID)
     */
    RemoteQueue getQueue(UUID computation) throws RemoteException;

    /**
     * Starts a new computation with the given ID on remote node. It creates
     * a new {@link org.sybila.parasim.computation.lifecycle.api.annotations.ComputationScope} context assigned to the remote node.
     *
     * @param status status of the master node which is used to monitor the computation
     * @param computation computation IDg
     * @throws RemoteException
     */
    void startComputation(RemoteMutableStatus status, UUID computation) throws RemoteException;

}
