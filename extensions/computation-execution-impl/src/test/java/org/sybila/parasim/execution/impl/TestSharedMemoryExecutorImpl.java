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
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.execution.AbstractExecutionTest;
import org.sybila.parasim.execution.api.ComputationEmitter;
import org.sybila.parasim.execution.api.Execution;
import org.sybila.parasim.execution.api.SharedMemoryExecutor;
import org.sybila.parasim.execution.conf.ExecutionConfiguration;
import org.sybila.parasim.model.Mergeable;
import org.sybila.parasim.model.MergeableBox;
import org.sybila.parasim.model.computation.AbstractComputation;
import org.sybila.parasim.model.computation.Computation;
import org.sybila.parasim.model.computation.ComputationId;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestSharedMemoryExecutorImpl extends AbstractExecutionTest {

    private static final int MAX_THREADS = 10;

    @Test
    public void testExecute() throws InterruptedException, ExecutionException, TimeoutException {
        super.testExecute(createSharedMemoryExecution(new TestIntegerComputation(0)), createExpectedResult());
    }

    @Test void testExecute2() throws InterruptedException, ExecutionException, TimeoutException {
        super.testExecute(createSharedMemoryExecution(new TestIntegerCompuationLimitedInstances(0)), new MergeableInteger(0));
    }

    @Test
    public void testAbort() throws InterruptedException, ExecutionException, TimeoutException {
        super.testAbort(createSharedMemoryExecution(new TestIntegerComputation(100000)));
    }

    @Test
    public void testEmitter() throws InterruptedException, ExecutionException, TimeoutException {
        Execution<MaxMergeableIntegerBox> result = (Execution<MaxMergeableIntegerBox>) createSharedMemoryExecution(new TestMaxIntegerComputation());
        Assert.assertEquals(TestMaxIntegerComputation.TO_EMIT.get(), result.execute().full().get().get());
    }

    protected <L extends Mergeable<L>> Execution<L> createSharedMemoryExecution(Computation<L> computation) {
        return getManager().resolve(SharedMemoryExecutor.class, Default.class, getManager().getRootContext()).submit(computation);
    }

    protected MergeableInteger createExpectedResult() {
        int expected = 0;
        for (int i=1; i<getManager().resolve(ExecutionConfiguration.class, Default.class, getManager().getRootContext()).getNumberOfThreadsInSharedMemory(); i++) {
            expected += i;
        }
        return new MergeableInteger(expected);
    }

    private static class MaxMergeableIntegerBox extends MergeableBox<MaxMergeableIntegerBox, Integer> {

        public MaxMergeableIntegerBox(Integer load) {
            super(load);
        }

        @Override
        public MaxMergeableIntegerBox merge(MaxMergeableIntegerBox toMerge) {
            return new MaxMergeableIntegerBox(Math.max(get(), toMerge.get()));
        }

    }

    private static class TestMaxIntegerComputation extends AbstractComputation<MaxMergeableIntegerBox> {

        private static final MaxMergeableIntegerBox TO_EMIT = new MaxMergeableIntegerBox(100000);

        @Inject
        private ComputationEmitter emitter;
        @Inject
        private ComputationId computationId;

        @Override
        public Computation<MaxMergeableIntegerBox> cloneComputation() {
            return new TestMaxIntegerComputation();
        }

        @Override
        public MaxMergeableIntegerBox call() throws Exception {
            emitter.emit(new TestEmittedComputation());
            return new MaxMergeableIntegerBox(computationId.currentId());
        }

        private static class TestEmittedComputation extends AbstractComputation<MaxMergeableIntegerBox> {

            @Override
            public Computation<MaxMergeableIntegerBox> cloneComputation() {
                return new TestEmittedComputation();
            }

            @Override
            public MaxMergeableIntegerBox call() throws Exception {
                return TestMaxIntegerComputation.TO_EMIT;
            }

        }

    }

}
