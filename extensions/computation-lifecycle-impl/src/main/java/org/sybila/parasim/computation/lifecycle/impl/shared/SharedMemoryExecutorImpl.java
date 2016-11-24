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
package org.sybila.parasim.computation.lifecycle.impl.shared;

import org.sybila.parasim.computation.lifecycle.impl.common.ComputationFuture;
import org.sybila.parasim.computation.lifecycle.impl.common.Mucker;
import org.sybila.parasim.computation.lifecycle.impl.common.SimpleOfferer;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import org.sybila.parasim.computation.lifecycle.api.Computation;
import org.sybila.parasim.computation.lifecycle.api.Emitter;
import org.sybila.parasim.computation.lifecycle.api.Future;
import org.sybila.parasim.computation.lifecycle.api.MutableStatus;
import org.sybila.parasim.computation.lifecycle.api.Offerer;
import org.sybila.parasim.computation.lifecycle.api.SharedMemoryExecutor;
import org.sybila.parasim.computation.lifecycle.api.Status;
import org.sybila.parasim.computation.lifecycle.api.annotations.ComputationScope;
import org.sybila.parasim.computation.lifecycle.api.annotations.Node;
import org.sybila.parasim.computation.lifecycle.impl.common.CallableFactory;
import org.sybila.parasim.computation.lifecycle.impl.common.AbstractExecutor;
import org.sybila.parasim.computation.lifecycle.impl.common.ComputationLifecycleConfiguration;
import org.sybila.parasim.computation.lifecycle.impl.common.ComputationUtils;
import org.sybila.parasim.core.annotation.Default;
import org.sybila.parasim.core.api.Binder;
import org.sybila.parasim.core.api.Context;
import org.sybila.parasim.core.api.enrichment.Enrichment;
import org.sybila.parasim.model.Mergeable;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class SharedMemoryExecutorImpl extends AbstractExecutor implements SharedMemoryExecutor {

    public SharedMemoryExecutorImpl(Enrichment enrichment, Context context) {
        super(enrichment, context);
    }

    @Override
    public <M extends Mergeable<M>> Future<M> submit(Computation<M> computation) {
        // init context
        Context context = getContext().context(ComputationScope.class);
        UUID node = UUID.randomUUID();
        ComputationLifecycleConfiguration configuration = context.resolve(ComputationLifecycleConfiguration.class, Default.class);
        // prepare services
        MutableStatus status = new SimpleStatus();
        Offerer offerer = new SimpleOfferer(
                node,
                status,
                context.resolve(Enrichment.class, Default.class).enrich(ComputationUtils.getOffererSelector(computation.getClass()), context),
                context.resolve(Enrichment.class, Default.class).enrich(ComputationUtils.getBalancerSelector(computation.getClass()), context));
        ExecutorService executorService = context.resolve(ExecutorService.class, Default.class);
        CallableFactory callableFactory = new CallableFactory(context, status);
        ComputationFuture<M> future = new ComputationFuture<>(node, context, status);
        Mucker mucker = new Mucker(node, status, executorService, offerer, configuration.getNodeThreshold(), callableFactory);
        // bind services
        Binder binder = context.resolve(Binder.class, Default.class);
        binder.bind(Emitter.class, Default.class, offerer);
        binder.bind(Status.class, Default.class, status);
        binder.bind(UUID.class, Node.class, node);
        // register progress listeners
        status.addProgressListerner(offerer);
        status.addProgressListerner(future);
        status.addProgressListerner(mucker);
        // emit computation
        offerer.emit(computation);
        // return future
        return future;
    }

}
