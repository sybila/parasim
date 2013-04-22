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
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.computation.lifecycle.api.Computation;
import org.sybila.parasim.computation.lifecycle.api.ProgressAdapter;
import org.sybila.parasim.computation.lifecycle.api.RemoteDescriptor;
import org.sybila.parasim.computation.lifecycle.api.RemoteQueue;
import org.sybila.parasim.computation.lifecycle.impl.common.ComputationLifecycleConfiguration;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class DistributedMemoryMucker extends ProgressAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DistributedMemoryMucker.class);

    private final Map<UUID, RemoteDescriptor> descriptors;
    private final SortedSet<RemoteDescriptor> sortedDescriptors;
    private final ComputationLifecycleConfiguration configuration;

    public DistributedMemoryMucker(ComputationLifecycleConfiguration configuration, Map<UUID, RemoteQueue> queues) {
        Validate.notNull(queues, "The map with queues can't be null.");
        Validate.isTrue(!queues.isEmpty(), "The map with queues can't be empty.");
        this.descriptors = new HashMap<>(queues.size());
        for (Map.Entry<UUID, RemoteQueue> entry: queues.entrySet()) {
            this.descriptors.put(entry.getKey(), new RemoteDescriptor(entry.getValue(), entry.getKey()));
        }
        this.sortedDescriptors = new TreeSet<>(descriptors.values());
        this.configuration = configuration;
    }

    @Override
    public void computing(UUID node, java.util.concurrent.Future event) {
        RemoteDescriptor descriptor = descriptors.get(node);
        sortedDescriptors.remove(descriptor);
        descriptor.getSize().decrementAndGet();
        sortedDescriptors.add(descriptor);
        rebalance();
    }

    @Override
    public void emitted(UUID node, Computation event) {
        RemoteDescriptor descriptor = descriptors.get(node);
        sortedDescriptors.remove(descriptor);
        descriptor.getSize().incrementAndGet();
        sortedDescriptors.add(descriptor);
        rebalance();
    }

    protected void rebalance() {
        final RemoteDescriptor busy = sortedDescriptors.last();
        final RemoteDescriptor idle = sortedDescriptors.first();
        if (busy.getSize().get() >= configuration.getBalancerBusyBound() && idle.getSize().get() <= configuration.getBalancerIdleBound() && busy.getSize().get() > configuration.getBalancerMultiplier() * idle.getSize().get()) {
            try {
                Computation toBalance = busy.getQueue().balance();
                if (toBalance != null) {
                    idle.getQueue().balance(toBalance);
                }
            } catch (RemoteException e) {
                throw new IllegalStateException("Can't balance the computation.", e);
            }
            sortedDescriptors.remove(busy);
            sortedDescriptors.remove(idle);
            busy.getSize().decrementAndGet();
            idle.getSize().incrementAndGet();
            sortedDescriptors.add(busy);
            sortedDescriptors.add(idle);
            StringBuilder builder = new StringBuilder("balancing: ");
            for (RemoteDescriptor descriptor: descriptors.values()) {
                builder.append(descriptor.getId()).append(" => ").append(descriptor.getSize().get()).append(" ");
            }
            LOGGER.info(builder.toString());
        }
    }

}
