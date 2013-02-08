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
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.sybila.parasim.computation.lifecycle.api.Computation;
import org.sybila.parasim.computation.lifecycle.api.MutableStatus;
import org.sybila.parasim.computation.lifecycle.api.ProgressListener;
import org.sybila.parasim.model.Mergeable;

/**
 * TODO: contract
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class SimpleStatus implements MutableStatus {

    private final AtomicLong remaining = new AtomicLong(0);
    private final AtomicLong done = new AtomicLong(0);
    private final AtomicLong computing = new AtomicLong(0);
    private final AtomicBoolean finished = new AtomicBoolean(false);
    private final List<ProgressListener> listeners = new ArrayList<>();

    @Override
    public void compute(Future event) {
        synchronized(listeners) {
            for (ProgressListener listener: listeners) {
                listener.computing(event);
            }
        }
        computing.incrementAndGet();
    }

    @Override
    public void done(Mergeable event) {
        synchronized(listeners) {
            for (ProgressListener listener: listeners) {
                listener.done(event);
            }
        }
        computing.decrementAndGet();
        done.incrementAndGet();
        if (remaining.decrementAndGet() == 0) {
            finish(event);
        }
    }

    @Override
    public void emit(Computation event) {
        synchronized(listeners) {
            for (ProgressListener listener: listeners) {
                listener.emitted(event);
            }
        }
        remaining.incrementAndGet();
    }

    @Override
    public long getDone() {
        return done.get();
    }

    @Override
    public long getRemaining() {
        return remaining.get();
    }

    @Override
    public long getComputing() {
        return computing.get();
    }

    @Override
    public void addProgressListerner(ProgressListener progressListener) {
        synchronized(listeners) {
            listeners.add(progressListener);
        }
    }

    @Override
    public boolean isFinished() {
        return finished.get();
    }

    private void finish(Mergeable event) {
        synchronized(listeners) {
            for (ProgressListener listener: listeners) {
                listener.finished(event);
            }
        }
        finished.set(true);
    }

}
