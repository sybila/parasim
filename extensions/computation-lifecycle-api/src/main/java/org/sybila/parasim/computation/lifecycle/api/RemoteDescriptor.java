package org.sybila.parasim.computation.lifecycle.api;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class RemoteDescriptor implements Comparable<RemoteDescriptor> {

    private final RemoteQueue queue;
    private final UUID id;
    private final AtomicLong size = new AtomicLong();

    public RemoteDescriptor(RemoteQueue queue, UUID id) {
        this.queue = queue;
        this.id = id;
    }

    @Override
    public int compareTo(RemoteDescriptor other) {
        int toReturn = (int) (size.get() - other.size.get());
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
        return "{" + size.get() + "}" + id.toString();
    }

    public UUID getId() {
        return id;
    }

    public AtomicLong getSize() {
        return size;
    }

    public RemoteQueue getQueue() {
        return queue;
    }

}