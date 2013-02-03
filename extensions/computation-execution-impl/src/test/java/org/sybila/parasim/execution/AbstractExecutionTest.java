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
package org.sybila.parasim.execution;

import org.sybila.parasim.model.computation.Computation;
import org.sybila.parasim.model.computation.ComputationId;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.sybila.parasim.execution.api.Execution;
import org.sybila.parasim.model.Mergeable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sybila.parasim.execution.impl.TestSequentialExecution;
import org.sybila.parasim.model.computation.AbstractComputation;
import org.sybila.parasim.core.annotation.Inject;
import org.sybila.parasim.core.test.ParasimTest;
import org.sybila.parasim.execution.api.annotations.NumberOfInstances;
import org.sybila.parasim.model.MergeableBox;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class AbstractExecutionTest extends ParasimTest {

    protected <R extends Mergeable<R>> void testExecute(Execution<R> execution, R expected) throws InterruptedException, ExecutionException, TimeoutException {
        assertEquals(execution.execute().full().get(2, TimeUnit.SECONDS), expected);
    }

    protected void testAbort(Execution execution) throws InterruptedException, ExecutionException, TimeoutException {
        Future<MergeableString> result = execution.execute().full();
        result.cancel(true);
        try {
            result.get(2, TimeUnit.SECONDS);
            fail("The execution has been canceled, so the Future.get() method should throw exception.");
        } catch(CancellationException e) {
        }
    }

    protected static class MergeableString extends MergeableBox<MergeableString, String> {
        public MergeableString(String original) {
            super(original);
        }
        @Override
        public MergeableString merge(MergeableString toMerge) {
            return new MergeableString(get() + toMerge.get());
        }
    }

    protected static class MergeableInteger extends MergeableBox<MergeableInteger, Integer> {

        public MergeableInteger(Integer load) {
            super(load);
        }

        @Override
        public MergeableInteger merge(MergeableInteger toMerge) {
            return new MergeableInteger(get() + toMerge.get());
        }

    }

    protected static class TestStringComputation extends AbstractComputation<MergeableString> {
        private String message;
        private long delay;
        @Inject
        private ComputationId threadId;
        public TestStringComputation(String message, long delay) {
            this.message = message;
            this.delay = delay;
        }
        @Override
        public MergeableString call() {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                Logger.getLogger(TestSequentialExecution.class.getName()).log(Level.SEVERE, null, ex);
            }
            return new MergeableString(threadId.currentId() + message);
        }
        @Override
        public Computation<MergeableString> cloneComputation() {
            return new TestStringComputation(message, delay);
        }
    }

    protected static class TestIntegerComputation extends AbstractComputation<MergeableInteger> {
        private final long delay;
        @Inject
        private ComputationId threadId;
        public TestIntegerComputation(long delay) {
            this.delay = delay;
        }
        @Override
        public MergeableInteger call() {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                Logger.getLogger(TestSequentialExecution.class.getName()).log(Level.SEVERE, null, ex);
            }
            return new MergeableInteger(threadId.currentId());
        }
        @Override
        public Computation<MergeableInteger> cloneComputation() {
            return new TestIntegerComputation(delay);
        }
    }

    @NumberOfInstances(1)
    protected static class TestIntegerCompuationLimitedInstances extends AbstractComputation<MergeableInteger> {
        private final long delay;
        @Inject
        private ComputationId threadId;

        public TestIntegerCompuationLimitedInstances(long delay) {
            this.delay = delay;
        }

        @Override
        public MergeableInteger call() {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                Logger.getLogger(TestSequentialExecution.class.getName()).log(Level.SEVERE, null, ex);
            }
            return new MergeableInteger(threadId.currentId());
        }

        @Override
        public Computation<MergeableInteger> cloneComputation() {
            return new TestIntegerCompuationLimitedInstances(delay);
        }

    }
}
