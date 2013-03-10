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

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface RemoteEmitter extends Remote {

    /**
     * Emits a new computation instance.
     *
     * @param computation emitted computation instance
     * @throws RemoteException
     */
    void emit(Computation computation) throws RemoteException;

    /**
     * Reschedules the given computation instance. The instance has to be
     * already emitted.
     *
     * @param computation computation instance to be rescheduled
     * @throws RemoteException
     */
    void balance(Computation computation) throws RemoteException;

}
