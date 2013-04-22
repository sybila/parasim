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

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class DistributedMemoryMucker extends ProgressAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DistributedMemoryMucker.class);

    private final Map<UUID, RemoteDescriptor> descriptors;
    private final SortedSet<RemoteDescriptor> sortedDescriptors;
    private final float balancerThreshold;

    public DistributedMemoryMucker(float balancerThreshold, Map<UUID, RemoteQueue> queues) {
        Validate.notNull(queues, "The map with queues can't be null.");
        Validate.isTrue(!queues.isEmpty(), "The map with queues can't be empty.");
        this.descriptors = new HashMap<>(queues.size());
        for (Map.Entry<UUID, RemoteQueue> entry: queues.entrySet()) {
            this.descriptors.put(entry.getKey(), new RemoteDescriptor(entry.getValue(), entry.getKey()));
        }
        this.sortedDescriptors = new TreeSet<>(descriptors.values());
        this.balancerThreshold = balancerThreshold;
    }

    @Override
    public void computing(UUID node, java.util.concurrent.Future event) {
        RemoteDescriptor descriptor = descriptors.get(node);
        sortedDescriptors.remove(descriptor);
        descriptor.getSize().decrementAndGet();
        sortedDescriptors.add(descriptor);
        reschedule();
    }

    @Override
    public void emitted(UUID node, Computation event) {
        RemoteDescriptor descriptor = descriptors.get(node);
        sortedDescriptors.remove(descriptor);
        descriptor.getSize().incrementAndGet();
        sortedDescriptors.add(descriptor);
        reschedule();
    }

    protected void reschedule() {
        final RemoteDescriptor busy = sortedDescriptors.last();
        final RemoteDescriptor notBusy = sortedDescriptors.first();
        if (busy.getSize().get() > 1 && notBusy.getSize().get() <= 1 && busy.getSize().get() > balancerThreshold * notBusy.getSize().get()) {
            LOGGER.debug("starting balancing from [" + busy.getSize() + "] to [" + notBusy.getSize() + "]");
            try {
                Computation toBalance = busy.getQueue().balance();
                LOGGER.debug("balancing " + toBalance);
                if (toBalance != null) {
                    notBusy.getQueue().balance(toBalance);
                }
                LOGGER.debug("balanced " + toBalance);
            } catch (RemoteException e) {
                throw new IllegalStateException("Can't balance the computation.", e);
            }
            sortedDescriptors.remove(busy);
            sortedDescriptors.remove(notBusy);
            busy.getSize().decrementAndGet();
            notBusy.getSize().incrementAndGet();
            sortedDescriptors.add(busy);
            sortedDescriptors.add(notBusy);
            StringBuilder builder = new StringBuilder("rescheduling: ");
            for (RemoteDescriptor descriptor: descriptors.values()) {
                builder.append(descriptor.getId()).append(" => ").append(descriptor.getSize().get()).append(" ");
            }
            LOGGER.info(builder.toString());
        }
    }

}
