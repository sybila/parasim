/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.computation.lifecycle.api.Computation;
import org.sybila.parasim.computation.lifecycle.api.MutableStatus;
import org.sybila.parasim.computation.lifecycle.api.ProgressListener;
import org.sybila.parasim.computation.lifecycle.api.RemoteMutableStatus;
import org.sybila.parasim.model.Mergeable;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class SlaveMutableStatus implements MutableStatus {

    private static final Logger LOGGER = LoggerFactory.getLogger(SlaveMutableStatus.class);

    private final List<ProgressListener> listeners = new ArrayList<>();
    private final RemoteMutableStatus remoteStatus;

    public SlaveMutableStatus(RemoteMutableStatus remoteStatus) {
        this.remoteStatus = remoteStatus;
    }

    @Override
    public void compute(UUID node, Future event) {
        try {
            remoteStatus.compute(node, null);
        } catch (RemoteException e) {
            throw new IllegalStateException("Can't invoke the remote status.", e);
        }
        synchronized(listeners) {
            for (ProgressListener listener: listeners) {
                listener.computing(node, event);
            }
        }
    }

    @Override
    public void done(UUID node, Mergeable event) {
        try {
            remoteStatus.done(node, event);
        } catch (RemoteException e) {
            throw new IllegalStateException("Can't invoke the remote status.", e);
        }
        synchronized(listeners) {
            for (ProgressListener listener: listeners) {
                listener.done(node, event);
            }
        }
    }

    @Override
    public void emit(UUID node, Computation event) {
        try {
            remoteStatus.emit(node, event);
        } catch (RemoteException e) {
            throw new IllegalStateException("Can't invoke the remote status.", e);
        }
        synchronized(listeners) {
            for (ProgressListener listener: listeners) {
                listener.emitted(node, event);
            }
        }
    }

    @Override
    public void balance(UUID node, Computation event) {
        try {
            remoteStatus.balance(node, null);
        } catch (RemoteException e) {
            throw new IllegalStateException("Can't invoke the remote status.", e);
        }
        synchronized(listeners) {
            for (ProgressListener listener: listeners) {
                listener.balanced(node, event);
            }
        }
    }

    @Override
    public long getDone() {
        try {
            return remoteStatus.getDone();
        } catch (RemoteException e) {
            throw new IllegalStateException("Can't invoke the remote status.", e);
        }
    }

    @Override
    public long getRemaining() {
        try {
            return remoteStatus.getRemaining();
        } catch (RemoteException e) {
            throw new IllegalStateException("Can't invoke the remote status.", e);
        }
    }

    @Override
    public long getComputing() {
        try {
            return remoteStatus.getComputing();
        } catch (RemoteException e) {
            throw new IllegalStateException("Can't invoke the remote status.", e);
        }
    }

    @Override
    public void addProgressListerner(ProgressListener progressListener) {
        synchronized(listeners) {
            listeners.add(progressListener);
        }
    }

    @Override
    public boolean isFinished() {
        try {
            return remoteStatus.isFinished();
        } catch (RemoteException e) {
            throw new IllegalStateException("Can't invoke the remote status.", e);
        }
    }
}
