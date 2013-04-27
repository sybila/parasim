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

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.computation.lifecycle.api.Computation;
import org.sybila.parasim.computation.lifecycle.api.DistributedMemoryExecutor;
import org.sybila.parasim.computation.lifecycle.api.Future;
import org.sybila.parasim.computation.lifecycle.api.MutableStatus;
import org.sybila.parasim.computation.lifecycle.api.ProgressAdapter;
import org.sybila.parasim.computation.lifecycle.api.RemoteDescriptor;
import org.sybila.parasim.computation.lifecycle.api.RemoteExecutor;
import org.sybila.parasim.computation.lifecycle.api.RemoteMutableStatus;
import org.sybila.parasim.computation.lifecycle.api.annotations.ComputationScope;
import org.sybila.parasim.computation.lifecycle.impl.common.AbstractExecutor;
import org.sybila.parasim.computation.lifecycle.impl.common.ComputationFuture;
import org.sybila.parasim.computation.lifecycle.impl.common.ComputationLifecycleConfiguration;
import org.sybila.parasim.computation.lifecycle.impl.shared.SimpleStatus;
import org.sybila.parasim.core.annotation.Default;
import org.sybila.parasim.core.api.Context;
import org.sybila.parasim.core.api.enrichment.Enrichment;
import org.sybila.parasim.model.Mergeable;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class DistributedMemoryExecutorImpl extends AbstractExecutor implements DistributedMemoryExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DistributedMemoryExecutorImpl.class);
    private final Collection<RemoteExecutor> remoteExecutors;

    public DistributedMemoryExecutorImpl(Enrichment enrichment, Context context, Collection<RemoteExecutor> remoteExecutors) {
        super(enrichment, context);
        Validate.notNull(remoteExecutors);
        this.remoteExecutors = new ArrayList<>(remoteExecutors);
    }

    @Override
    public <M extends Mergeable<M>> Future<M> submit(Computation<M> computation) {
        // init context
        Context context = getContext().context(ComputationScope.class);
        UUID computationId = UUID.randomUUID();

        // prepare status
        MutableStatus status = new SimpleStatus();
        RemoteMutableStatus remoteStatus = new RemoteMutableStatusWrapper(status);
        RemoteMutableStatus exportedRemoteStatus = null;
        Map<UUID, RemoteDescriptor> remoteDescriptors = new HashMap<>(remoteExecutors.size());

        try {
            exportedRemoteStatus = (RemoteMutableStatus) UnicastRemoteObject.exportObject(remoteStatus);
            // start the computation on slave nodes
            for (RemoteExecutor executor: remoteExecutors) {
                    executor.startComputation(computation.getClass(), exportedRemoteStatus, computationId);
                    remoteDescriptors.put(executor.getId(), new RemoteDescriptor(executor.getHost(), executor.getQueue(computationId), executor.getId()));
            }
            // prepare services
            ComputationFuture<M> future = new ComputationFuture<>(computationId, context, status);
            DistributedMemoryMucker mucker = new DistributedMemoryMucker(context.resolve(ComputationLifecycleConfiguration.class, Default.class), remoteDescriptors);
            // register progress listeners
            status.addProgressListerner(mucker);
            status.addProgressListerner(new RemoteComputationDestroyer(remoteStatus, computationId));
            status.addProgressListerner(future);
            // start the computation
            remoteExecutors.iterator().next().getQueue(computationId).emit(computation);
            // return future
            return future;
        } catch (RemoteException e) {
            try {
                if (exportedRemoteStatus != null) {
                    UnicastRemoteObject.unexportObject(remoteStatus, true);
                }
            } catch (NoSuchObjectException ee) {
                LOGGER.error(ee.getMessage(), ee);
            } finally {
                for (RemoteExecutor executor: remoteExecutors) {
                    try {
                        if (remoteDescriptors.containsKey(executor.getId())) {
                            executor.destroyComputation(computationId);
                        }
                    } catch (RemoteException ee) {
                        LOGGER.error(ee.getMessage(), ee);
                    }
                }
                try {
                    context.destroy();
                } catch (Exception ee) {
                    LOGGER.error("Can't destroy the computation context.");
                }
                throw new IllegalStateException("Can't submit the computation.", e);
            }
        }



    }

    private class RemoteComputationDestroyer extends ProgressAdapter {

        private final UUID id;
        private final RemoteMutableStatus remoteStatus;

        public RemoteComputationDestroyer(RemoteMutableStatus remoteStatus, UUID id) {
            this.remoteStatus = remoteStatus;
            this.id = id;
        }

        @Override
        public void finished(UUID node, Mergeable event) {
            for (RemoteExecutor executor: remoteExecutors) {
                try {
                    executor.destroyComputation(id);
                } catch(Exception e) {
                    LOGGER.error("Can't destroy the remote computation.", e);
                }
            }
            try {
                UnicastRemoteObject.unexportObject(remoteStatus, true);
            } catch (NoSuchObjectException e) {
                throw new IllegalStateException("Can't unexport remote mutable status.", e);
            }
        }

    }

}
