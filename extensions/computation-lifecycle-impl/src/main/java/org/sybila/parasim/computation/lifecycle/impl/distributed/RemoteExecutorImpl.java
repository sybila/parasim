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

import org.sybila.parasim.computation.lifecycle.api.RemoteExecutor;
import org.sybila.parasim.computation.lifecycle.api.RemoteMutableStatus;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.computation.lifecycle.api.Computation;
import org.sybila.parasim.computation.lifecycle.api.Emitter;
import org.sybila.parasim.computation.lifecycle.api.MutableStatus;
import org.sybila.parasim.computation.lifecycle.api.Offerer;
import org.sybila.parasim.computation.lifecycle.api.RemoteQueue;
import org.sybila.parasim.computation.lifecycle.api.annotations.ComputationScope;
import org.sybila.parasim.computation.lifecycle.api.annotations.Node;
import org.sybila.parasim.computation.lifecycle.api.annotations.Original;
import org.sybila.parasim.computation.lifecycle.impl.common.CallableFactory;
import org.sybila.parasim.computation.lifecycle.impl.common.ComputationLifecycleConfiguration;
import org.sybila.parasim.computation.lifecycle.impl.common.ComputationUtils;
import org.sybila.parasim.computation.lifecycle.impl.common.SimpleOfferer;
import org.sybila.parasim.computation.lifecycle.impl.common.Mucker;
import org.sybila.parasim.core.annotation.Default;
import org.sybila.parasim.core.api.Binder;
import org.sybila.parasim.core.api.Context;
import org.sybila.parasim.core.api.enrichment.Enrichment;
import org.sybila.parasim.core.impl.remote.RemoteConfiguration;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class RemoteExecutorImpl implements RemoteExecutor {

    private final Context context;
    private final Map<UUID, Context> contexts = new HashMap<>();
    private final UUID id = UUID.randomUUID();

    // for proxies
    protected RemoteExecutorImpl() {
        this.context = null;
    }

    public RemoteExecutorImpl(Context context) {
        Validate.notNull(context);
        this.context = context;
    }

    @Override
    public synchronized void destroyComputation(UUID id) throws RemoteException {
        Validate.notNull(id);
        Context ctxt = contexts.get(id);
        if (ctxt == null) {
            throw new IllegalStateException("The computation " + id + " has already started.");
        }
        // clean exported objects
        UnicastRemoteObject.unexportObject(ctxt.resolve(RemoteQueue.class, Original.class), true);
        if (ctxt != null) {
            try {
                ctxt.destroy();
            } catch (Exception e) {
                throw new IllegalStateException("Can't destroy context.", e);
            }
        }
        contexts.remove(id);
    }

    @Override
    public UUID getId() throws RemoteException {
        return id;
    }

    @Override
    public String getHost() throws RemoteException {
        return context.resolve(RemoteConfiguration.class, Default.class).getHost();
    }

    @Override
    public synchronized RemoteQueue getQueue(UUID computation) throws RemoteException {
        Validate.notNull(computation);
        if (contexts.containsKey(id)) {
            throw new IllegalStateException("The computation " + id + " has already started.");
        }
        Context ctxt = contexts.get(computation);
        return ctxt.resolve(RemoteQueue.class, Default.class);
    }

    @Override
    public synchronized void startComputation(Class<? extends Computation> computationClass, RemoteMutableStatus status, UUID id) throws RemoteException {
        Validate.notNull(context);
        Validate.notNull(id);
        if (contexts.containsKey(id)) {
            throw new IllegalStateException("The computation " + id + " has already started.");
        }
        Context ctxt = context.context(ComputationScope.class);
        initServices(computationClass, ctxt, status);
        contexts.put(id, ctxt);
    }

    private void initServices(Class<? extends Computation> computationClass, Context context, RemoteMutableStatus remoteStatus) throws RemoteException {
        Binder binder = (Binder) context;
        // initialize services
        MutableStatus localStatus = new SlaveMutableStatus(remoteStatus);
        CallableFactory callableFactory = new CallableFactory(context, localStatus);
        Offerer offerer = new SimpleOfferer(
                getId(),
                localStatus,
                context.resolve(Enrichment.class, Default.class).enrich(ComputationUtils.getOffererSelector(computationClass), context),
                context.resolve(Enrichment.class, Default.class).enrich(ComputationUtils.getBalancerSelector(computationClass), context));
        ComputationLifecycleConfiguration configuration = context.resolve(ComputationLifecycleConfiguration.class, Default.class);
        RemoteQueue remoteQueue = new RemoteQueueWrapper(offerer, offerer);
        Mucker mucker = new Mucker(id, localStatus, context.resolve(ExecutorService.class, Default.class), offerer, configuration.getNodeThreshold(), callableFactory);

        // add progress listeners
        localStatus.addProgressListerner(offerer);
        localStatus.addProgressListerner(mucker);

        // bind services
        binder.bind(MutableStatus.class, Default.class, localStatus);
        binder.bind(Offerer.class, Default.class, offerer);
        binder.bind(Emitter.class, Default.class, offerer);
        binder.bind(RemoteQueue.class, Original.class, remoteQueue);
        binder.bind(
                RemoteQueue.class,
                Default.class,
                (RemoteQueue) UnicastRemoteObject.exportObject(remoteQueue));
        binder.bind(UUID.class, Node.class, getId());
    }

}
