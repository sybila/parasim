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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import net.jcip.annotations.GuardedBy;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.computation.lifecycle.api.Computation;
import org.sybila.parasim.computation.lifecycle.api.MutableStatus;
import org.sybila.parasim.computation.lifecycle.api.Offerer;
import org.sybila.parasim.computation.lifecycle.api.ProgressAdapter;
import org.sybila.parasim.model.Mergeable;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class Mucker extends ProgressAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Mucker.class);

    private final MutableStatus status;
    private final ExecutorService executorService;
    private final Offerer offerer;
    private final long threshold;
    private final CallableFactory callableFactory;
    private final UUID node;
    private final AtomicLong inExecutorService = new AtomicLong(0);

    public Mucker(UUID node, MutableStatus status, ExecutorService executorService, Offerer offerer, long threshold, CallableFactory callableFactory) {
        Validate.notNull(node, "The node ID can't be null.");
        Validate.notNull(status, "Status can't be null.");
        Validate.notNull(executorService, "Executor service can't be null.");
        Validate.notNull(offerer, "Oferrer can't be null.");
        Validate.notNull(callableFactory, "Callable factory can't be null.");
        Validate.isTrue(threshold > 0, "Threshold has to a positive number.");
        this.node = node;
        this.status = status;
        this.executorService = executorService;
        this.offerer = offerer;
        this.threshold = threshold;
        this.callableFactory = callableFactory;
    }

    @Override
    @GuardedBy("this")
    public void emitted(UUID node, Computation event) {
        if (this.node.equals(node)) {
            trySubmit();
        }
    }

    @Override
    @GuardedBy("this")
    public void computing(UUID node, java.util.concurrent.Future event) {
        if (this.node.equals(node)) {
            trySubmit();
        }
    }

    @Override
    @GuardedBy("this")
    public void balanced(UUID node, Computation event) {
        if (this.node.equals(node)) {
            trySubmit();
        }
    }

    @Override
    public void done(UUID node, Mergeable event) {
        if (this.node.equals(node)) {
            inExecutorService.decrementAndGet();
            trySubmit();
        }
    }

    private synchronized void trySubmit() {
        if (inExecutorService.get() < threshold) {
            Computation computation = offerer.poll();
            if (computation != null) {
                inExecutorService.incrementAndGet();
                status.compute(node,executorService.submit(callableFactory.instance(computation)));
            }
        }
    }

}
