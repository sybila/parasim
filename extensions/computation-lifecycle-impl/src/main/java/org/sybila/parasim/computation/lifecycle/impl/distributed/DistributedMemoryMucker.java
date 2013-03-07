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
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.computation.lifecycle.api.Computation;
import org.sybila.parasim.computation.lifecycle.api.ProgressAdapter;
import org.sybila.parasim.computation.lifecycle.api.RemoteQueue;
import org.sybila.parasim.model.Mergeable;

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
    public void done(UUID node, Mergeable event) {
        RemoteDescriptor descriptor = descriptors.get(node);
        sortedDescriptors.remove(descriptor);
        descriptor.size.decrementAndGet();
        sortedDescriptors.add(descriptor);
        reschedule();
    }

    @Override
    public void emitted(UUID node, Computation event) {
        RemoteDescriptor descriptor = descriptors.get(node);
        sortedDescriptors.remove(descriptor);
        descriptor.size.incrementAndGet();
        sortedDescriptors.add(descriptor);
        reschedule();
    }

    protected void reschedule() {
        final RemoteDescriptor busy = sortedDescriptors.last();
        final RemoteDescriptor notBusy = sortedDescriptors.first();
        if (busy.size.get() > 1 && busy.size.get() > balancerThreshold * notBusy.size.get()) {
            LOGGER.debug("starting balancing from [" + busy.size + "] to [" + notBusy.size + "]");
            try {
                Computation toBalance = busy.queue.balance();
                LOGGER.debug("balancing " + toBalance);
                if (toBalance != null) {
                    notBusy.queue.balance(toBalance);
                }
                LOGGER.debug("balanced " + toBalance);
            } catch (RemoteException e) {
                throw new IllegalStateException("Can't balance the computation.", e);
            }
            sortedDescriptors.remove(busy);
            sortedDescriptors.remove(notBusy);
            busy.size.decrementAndGet();
            notBusy.size.incrementAndGet();
            sortedDescriptors.add(busy);
            sortedDescriptors.add(notBusy);
        }

    }

    private static class RemoteDescriptor implements Comparable<RemoteDescriptor> {
        private final RemoteQueue queue;
        private final UUID id;
        private final AtomicLong size = new AtomicLong();

        public RemoteDescriptor(RemoteQueue queue, UUID id) {
            this.queue = queue;
            this.id = id;
        }

        @Override
        public int compareTo(RemoteDescriptor other) {
            int toReturn =  (int) (size.get() - other.size.get());
            if (toReturn == 0) {
                return id.compareTo(other.id);
            } else {
                return toReturn;
            }
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 29 * hash + Objects.hashCode(this.id);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final RemoteDescriptor other = (RemoteDescriptor) obj;
            if (!Objects.equals(this.id, other.id)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "{" + size.get() + "}"  + id.toString();
        }

    }

}
