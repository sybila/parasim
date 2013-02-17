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
package org.sybila.parasim.computation.lifecycle.impl.common;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.computation.lifecycle.api.Future;
import org.sybila.parasim.computation.lifecycle.api.MutableStatus;
import org.sybila.parasim.computation.lifecycle.api.ProgressAdapter;
import org.sybila.parasim.computation.lifecycle.api.Status;
import org.sybila.parasim.computation.lifecycle.api.annotations.ComputationScope;
import org.sybila.parasim.core.api.Context;
import org.sybila.parasim.model.Mergeable;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ComputationFuture<M extends Mergeable<M>> extends ProgressAdapter implements Future<M> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComputationFuture.class);

    private final Context context;
    private final MutableStatus status;
    private final UUID node;

    private boolean cancelled = false;
    private M partial;

    public ComputationFuture(UUID node, Context context, MutableStatus status) {
        if (!context.getScope().equals(ComputationScope.class)) {
            throw new IllegalStateException("The context has to be " + ComputationScope.class.getSimpleName());
        }
        this.context = context;
        this.status = status;
        this.node = node;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public M getPartial() throws InterruptedException, ExecutionException {
        return partial;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        try {
            context.destroy();
            cancelled = true;
        } catch (Exception e) {
            LOGGER.warn("An error happened during cancelling the computation.", e);
        }
        return cancelled;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean isDone() {
        return status.isFinished();
    }

    @Override
    public M get() throws InterruptedException, ExecutionException {
        if (!status.isFinished()) {
            synchronized (this) {
                while (!status.isFinished()) {
                    wait();
                }
            }
        }
        return partial;
    }

    @Override
    public M get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (!status.isFinished()) {
            synchronized (this) {
                long before = System.currentTimeMillis();
                while (!status.isFinished() && System.currentTimeMillis() - before < unit.toMillis(timeout)) {
                    wait(unit.toMillis(timeout) - (System.currentTimeMillis() - before));
                }
                if (!status.isFinished()) {
                    throw new TimeoutException();
                }
            }
        }
        return partial;
    }

    @Override
    public synchronized void done(UUID node, Mergeable event) {
        if (partial == null) {
            LOGGER.debug("the first partial result is " + event);
            partial = (M) event;
        } else {
            Mergeable before = partial;
            partial = partial.merge((M) event);
            LOGGER.debug("merging: " + before + " + " +  event + " = " + partial);
        }
    }

    @Override
    public void finished(UUID node, Mergeable event) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    synchronized (ComputationFuture.this) {
                        ComputationFuture.this.notifyAll();
                    }
                    context.destroy();
                } catch (Exception e) {
                    throw new IllegalStateException("Can't destroy the computation.", e);
                }
            }
        }, "computation-context-destroyer").start();
    }

}
