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
    private final String host;
    private final AtomicLong size = new AtomicLong();

    public RemoteDescriptor(String host, RemoteQueue queue, UUID id) {
        this.queue = queue;
        this.id = id;
        this.host = host;
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

    public String getHost() {
        return host;
    }

    public AtomicLong getSize() {
        return size;
    }

    public RemoteQueue getQueue() {
        return queue;
    }

}