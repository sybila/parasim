/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.computation.lifecycle.api.Computation;
import org.sybila.parasim.computation.lifecycle.api.ComputationContainer;
import org.sybila.parasim.computation.lifecycle.api.Emitter;
import org.sybila.parasim.computation.lifecycle.api.SharedMemoryExecutor;
import org.sybila.parasim.computation.lifecycle.impl.AbstractComputationLifecycleTest;
import org.sybila.parasim.computation.lifecycle.test.InfiniteSequentialFactorial;
import org.sybila.parasim.computation.lifecycle.test.MultiplicativeInteger;
import org.sybila.parasim.computation.lifecycle.test.ParallelFactorial;
import org.sybila.parasim.computation.lifecycle.test.SequentialFactorial;
import org.sybila.parasim.core.annotation.Default;
import org.sybila.parasim.core.annotation.Inject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestSharedMemoryComputationContainer extends AbstractComputationLifecycleTest {

    @Test
    public void testSeqFactorial() throws InterruptedException, ExecutionException, TimeoutException {
        checkSeqFactorial(new SequentialFactorial(10), 10);
    }

    @Test
    public void testParFactorial() throws InterruptedException, ExecutionException, TimeoutException {
        checkParFactorial(new ParallelFactorial(10), 10);
    }

    @Test
    public void testInfiniteSeqFactorial() throws InterruptedException, ExecutionException, TimeoutException {
        checkInfiniteSeqFactorial(new InfiniteSequentialFactorial(10), 10);
    }

    @Test
    public void testDestroy() throws InterruptedException, ExecutionException, TimeoutException {
        ComputationContainer container = getManager().resolve(ComputationContainer.class, Default.class);
        final int n = 10;
        container.compute(new DestroyableFactorial(n)).get();
        Assert.assertEquals(DestroyableFactorial.DESTROYED.get(), n);
    }

    @Override
    protected void beforeManagerCreated() {
        super.beforeManagerCreated();
        System.setProperty("parasim.computation.lifecycle.default.executor", SharedMemoryExecutor.class.getName());
    }

    public static class DestroyableFactorial implements Computation<MultiplicativeInteger> {

        private static final Logger LOGGER = LoggerFactory.getLogger(SequentialFactorial.class);
        private static final AtomicInteger DESTROYED = new AtomicInteger(0);

        private final Integer n;
        @Inject
        private Emitter emitter;

        public DestroyableFactorial(Integer n) {
            this.n = n;
        }

        @Override
        public MultiplicativeInteger call() throws Exception {
            LOGGER.debug(this + " started");
            if (n > 1) {
                emitter.emit(new DestroyableFactorial(n - 1));
            }
            LOGGER.debug(this + " finished");
            return new MultiplicativeInteger(n);
        }

        @Override
        public void destroy() throws Exception {
            DESTROYED.incrementAndGet();
        }

    }
}
