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
package org.sybila.parasim.computation.lifecycle.impl.shared;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.jcip.annotations.GuardedBy;
import org.sybila.parasim.computation.lifecycle.api.Computation;
import org.sybila.parasim.computation.lifecycle.api.MutableStatus;
import org.sybila.parasim.computation.lifecycle.api.Offerer;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class SimpleOfferer implements Offerer {

    @GuardedBy("this")
    private final List<Computation> computations = new ArrayList<>();
    @GuardedBy("this")
    private final List<Time> accesses = new ArrayList<>();
    private final MutableStatus status;

    public SimpleOfferer(MutableStatus status) {
        this.status = status;
    }

    @Override
    public synchronized Collection<Time> accesses() {
        return Collections.unmodifiableCollection(accesses);
    }

    @Override
    public synchronized Computation poll() {
        if (computations.isEmpty()) {
            return null;
        }
        accesses.add(new SimpleTime(System.currentTimeMillis()));
        return computations.remove(0);
    }

    @Override
    public synchronized Computation reschedule() {
        return computations.remove(0);
    }

    @Override
    public synchronized int size() {
        return computations.size();
    }

    @Override
    public void emit(Computation computation) {
        synchronized(this) {
            computations.add(computation);
        }
        status.emit(computation);
    }

    protected static final class SimpleTime implements Time {
        private final long value;

        public SimpleTime(long value) {
            this.value = value;
        }

        @Override
        public long value() {
            return value;
        }

    }

}
