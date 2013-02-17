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
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.computation.lifecycle.api.Computation;
import org.sybila.parasim.computation.lifecycle.api.MutableStatus;
import org.sybila.parasim.computation.lifecycle.api.ProgressListener;
import org.sybila.parasim.model.Mergeable;

/**
 * TODO: contract
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class SimpleStatus implements MutableStatus {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleStatus.class);

    private final AtomicLong remaining = new AtomicLong(0);
    private final AtomicLong done = new AtomicLong(0);
    private final AtomicLong computing = new AtomicLong(0);
    private final AtomicBoolean finished = new AtomicBoolean(false);
    private final List<ProgressListener> listeners = new ArrayList<>();

    @Override
    public void compute(UUID node, Future event) {
        computing.incrementAndGet();
        synchronized(listeners) {
            for (ProgressListener listener: listeners) {
                listener.computing(node, event);
            }
        }
    }

    @Override
    public void done(UUID node, Mergeable event) {
        synchronized(listeners) {
            for (ProgressListener listener: listeners) {
                listener.done(node, event);
            }
        }
        computing.decrementAndGet();
        done.incrementAndGet();
        if (remaining.decrementAndGet() == 0) {
            finish(node, event);
        }
    }

    @Override
    public void emit(UUID node, Computation event) {
        remaining.incrementAndGet();
        synchronized(listeners) {
            for (ProgressListener listener: listeners) {
                listener.emitted(node, event);
            }
        }
    }

    @Override
    public void reschedule(final UUID node, final Computation event) {
        new Thread() {

            @Override
            public void run() {
                synchronized(listeners) {
                    for (ProgressListener listener: listeners) {
                        listener.rescheduled(node, event);
                    }
                }
            }

        }.start();
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

    private void finish(UUID node, Mergeable event) {
        synchronized(listeners) {
            for (ProgressListener listener: listeners) {
                listener.finished(node, event);
            }
        }
        finished.set(true);
    }

}
