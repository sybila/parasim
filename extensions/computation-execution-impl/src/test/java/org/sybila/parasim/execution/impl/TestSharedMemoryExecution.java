/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.execution.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.sybila.parasim.core.ContextEvent;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.core.extension.enrichment.api.Enrichment;
import org.sybila.parasim.execution.AbstractExecutionTest;
import org.sybila.parasim.execution.api.ComputationContext;
import org.sybila.parasim.execution.api.Execution;
import org.sybila.parasim.model.Mergeable;
import org.sybila.parasim.model.computation.Computation;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestSharedMemoryExecution extends AbstractExecutionTest {

    private static final int MAX_THREADS = 10;

    @Test(enabled=false)
    public void testAbort() throws InterruptedException, ExecutionException, TimeoutException {
        super.testAbort(createSharedMemoryExecution(new TestIntegerComputation(100000)));
    }

    @Test
    public void testExecute() throws InterruptedException, ExecutionException, TimeoutException {
        super.testExecute(createSharedMemoryExecution(new TestIntegerComputation(0)), createExpectedResult());
    }

    protected <R extends Mergeable<R>> Execution<R> createSharedMemoryExecution(Computation<R> computation) {
        return SharedMemoryExecution.of(
            getManager().resolve(java.util.concurrent.Executor.class, Default.class, getManager().getRootContext()),
            computation,
            getManager().resolve(Enrichment.class, Default.class, getManager().getRootContext()),
            new ContextEvent<ComputationContext>() {
                public void initialize(ComputationContext context) {
                    context.setParent(getManager().getRootContext());
                    getManager().initializeContext(context);
                }
                public void finalize(ComputationContext context) {
                    getManager().finalizeContext(context);
                }
            },
            0,
            MAX_THREADS - 1,
            MAX_THREADS - 1
        );
    }

    protected MergeableInteger createExpectedResult() {
        int expected = 0;
        for (int i=1; i<MAX_THREADS; i++) {
            expected += i;
        }
        return new MergeableInteger(expected);
    }
}