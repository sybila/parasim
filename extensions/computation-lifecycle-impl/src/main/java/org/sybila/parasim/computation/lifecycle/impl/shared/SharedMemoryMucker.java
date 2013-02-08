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

import java.util.concurrent.ExecutorService;
import net.jcip.annotations.GuardedBy;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.computation.lifecycle.api.Computation;
import org.sybila.parasim.computation.lifecycle.api.MutableStatus;
import org.sybila.parasim.computation.lifecycle.api.Offerer;
import org.sybila.parasim.computation.lifecycle.api.ProgressAdapter;
import org.sybila.parasim.computation.lifecycle.impl.common.CallableFactory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class SharedMemoryMucker extends ProgressAdapter {

    private final MutableStatus status;
    private final ExecutorService executorService;
    private final Offerer offerer;
    private final long threshold;
    private final CallableFactory callableFactory;

    public SharedMemoryMucker(MutableStatus status, ExecutorService executorService, Offerer offerer, long threshold, CallableFactory callableFactory) {
        Validate.notNull(status, "Status can't be null.");
        Validate.notNull(executorService, "Executor service can't be null.");
        Validate.notNull(offerer, "Oferrer can't be null.");
        Validate.notNull(callableFactory, "Callable factory can't be null.");
        Validate.isTrue(threshold > 0, "Threshold has to a positive number.");
        this.status = status;
        this.executorService = executorService;
        this.offerer = offerer;
        this.threshold = threshold;
        this.callableFactory = callableFactory;
    }

    @Override
    @GuardedBy("this")
    public void emitted(Computation event) {
        trySubmit();
    }

    @Override
    @GuardedBy("this")
    public void computing(java.util.concurrent.Future event) {
        trySubmit();
    }

    private synchronized void trySubmit() {
        if (status.getComputing() < threshold) {
            Computation computation = offerer.poll();
            if (computation != null) {
               executorService.submit(callableFactory.instance(computation));
            }
        }
    }

}
