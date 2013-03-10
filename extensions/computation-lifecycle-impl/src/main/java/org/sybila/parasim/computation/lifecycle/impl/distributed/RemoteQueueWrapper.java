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
package org.sybila.parasim.computation.lifecycle.impl.distributed;

import java.rmi.RemoteException;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.computation.lifecycle.api.Computation;
import org.sybila.parasim.computation.lifecycle.api.Emitter;
import org.sybila.parasim.computation.lifecycle.api.Offerer;
import org.sybila.parasim.computation.lifecycle.api.RemoteQueue;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class RemoteQueueWrapper implements RemoteQueue {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteQueueWrapper.class);

    private final Offerer offerer;
    private final Emitter emitter;

    public RemoteQueueWrapper(Offerer offerer, Emitter emitter) {
        Validate.notNull(offerer);
        Validate.notNull(emitter);
        this.offerer = offerer;
        this.emitter = emitter;
    }

    @Override
    public Computation balance() throws RemoteException {
        Computation reschedule = offerer.balance();
        return reschedule;
    }

    @Override
    public int size() throws RemoteException {
        return offerer.size();
    }

    @Override
    public void emit(Computation computation) throws RemoteException {
        emitter.emit(computation);
    }

    @Override
    public void balance(final Computation computation) throws RemoteException {
        new Thread() {
            @Override
            public void run() {
                emitter.balance(computation);
            }
        }.start();
    }

}
