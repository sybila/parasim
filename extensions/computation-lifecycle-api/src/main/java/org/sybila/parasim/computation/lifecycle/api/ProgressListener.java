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
package org.sybila.parasim.computation.lifecycle.api;

import java.util.UUID;
import org.sybila.parasim.model.Mergeable;

/**
 * The progress listener can be used to control the computation and
 * is invoked by {@link MutableStatus}.
 *
 * There is a few contracts between {@link ProgressListener} and {@link MutableStatus},
 * see concrete method for more information.
 *
 * If you want to implement your own progress listener, it's recommended to use
 * {@link ProgressAdapter}.
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface ProgressListener {

    /**
     * The method is called when an execution of a new computation instance starts.
     * The listener is invoked before the number of computing instances is incremented.
     *
     * @param node node where the computation instance is computing
     * @param event future object representing a handler for the computation
     * @see Status#getComputing()
     * @see MutableStatus#compute(java.util.concurrent.Future)
     */
    void computing(UUID node, java.util.concurrent.Future event);

    /**
     * The method is called when a new computation instance is emitted.
     * The listener is invoked before the number of remaining computation instances
     * is incremented.
     *
     * @param node node where the computation instance is emitted
     * @param event emitted computation instance
     * @see Status#getRemaining()
     * @see MutableStatus#emit(org.sybila.parasim.computation.lifecycle.api.Computation)
     */
    void emitted(UUID node, Computation event);

    /**
     * The method is called when a computation instance completes its execution.
     * The listener is invoked before the numbers of computing, done and remaining instances
     * are decremented. If the given instance was the last one from the computation,
     * this method is invoked before the computation is marked as finished and
     * before {@link #finished(org.sybila.parasim.model.Mergeable) } method is called.
     *
     * @param node node where the computation instance completes its execution
     * @param event the result of the computation instance
     * @see Status#getDone()
     * @see MutableStatus#done(org.sybila.parasim.model.Mergeable)
     */
    void done(UUID node, Mergeable event);

    /**
     * The method is called when the last instance of the computation is done.
     * The listener is invoked after the numbers of computing, done and remaining instances
     * are decremented, after {@link #done(org.sybila.parasim.model.Mergeable) }
     * method invocation and before the computation is marked as finished.
     *
     * @param node node where the computation is finished
     * @param event the computation result
     * @see Status#isFinished()
     * @see MutableStatus#done(org.sybila.parasim.model.Mergeable)
     */
    void finished(UUID node, Mergeable event);

    /**
     * The method is called when an alrady emitted computation instance is rescheduled.
     *
     * @param node node which initiate the rescheduling
     * @param event rescheduled computation instance
     * @see MutableStatus#reschedule(org.sybila.parasim.computation.lifecycle.api.Computation)
     */
    void rescheduled(UUID node, Computation event);

}
