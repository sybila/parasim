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
import java.util.UUID;
import java.util.concurrent.Future;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.computation.lifecycle.api.Computation;
import org.sybila.parasim.computation.lifecycle.api.MutableStatus;
import org.sybila.parasim.computation.lifecycle.api.RemoteMutableStatus;
import org.sybila.parasim.model.Mergeable;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class RemoteMutableStatusWrapper implements RemoteMutableStatus {

    private final MutableStatus status;

    public RemoteMutableStatusWrapper(MutableStatus status) {
        Validate.notNull(status);
        this.status = status;
    }

    @Override
    public long getDone() throws RemoteException {
        return status.getDone();
    }

    @Override
    public long getComputing() throws RemoteException {
        return status.getComputing();
    }

    @Override
    public long getRemaining() throws RemoteException {
        return status.getRemaining();
    }

    @Override
    public boolean isFinished() throws RemoteException {
        return status.isFinished();
    }

    @Override
    public void compute(UUID node, Future event) throws RemoteException {
        status.compute(node, event);
    }

    @Override
    public void done(UUID node, Mergeable event) throws RemoteException {
        status.done(node, event);
    }

    @Override
    public void emit(UUID node, Computation computation) throws RemoteException {
        status.emit(node, computation);
    }

    @Override
    public void reschedule(UUID node, Computation computation) throws RemoteException {
        status.reschedule(node, computation);
    }

}
