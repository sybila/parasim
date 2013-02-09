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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import junit.framework.Assert;
import org.sybila.parasim.computation.lifecycle.api.Computation;
import org.sybila.parasim.computation.lifecycle.api.ComputationContainer;
import org.sybila.parasim.computation.lifecycle.api.Emitter;
import org.sybila.parasim.computation.lifecycle.api.Future;
import org.sybila.parasim.core.annotation.Default;
import org.sybila.parasim.core.annotation.Inject;
import org.sybila.parasim.core.test.ParasimTest;
import org.sybila.parasim.model.MergeableBox;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestDefaultComputationContainer extends ParasimTest {

    @Test
    public void testNotNull() {
        Assert.assertNotNull(getManager().resolve(ComputationContainer.class, Default.class));
    }

    @Test
    public void testSeqFactorial() throws InterruptedException, ExecutionException, TimeoutException {
        ComputationContainer container = getManager().resolve(ComputationContainer.class, Default.class);
        Assert.assertNotNull(container);
        int n = 10;
        SeqFactorialComputation computation = new SeqFactorialComputation(n);
        int expected = 1;
        for (int i=2; i<=n; i++) {
            expected *= i;
        }
        Assert.assertEquals(expected, (int)container.compute(computation).get(5, TimeUnit.SECONDS).get());
        Assert.assertEquals(expected, (int)container.compute(computation).get().get());
    }

    @Test
    public void testParFactorial() throws InterruptedException, ExecutionException, TimeoutException {
        ComputationContainer container = getManager().resolve(ComputationContainer.class, Default.class);
        Assert.assertNotNull(container);
        int n = 10;
        ParFactorialComputation computation = new ParFactorialComputation(n);
        int expected = 1;
        for (int i=2; i<=n; i++) {
            expected *= i;
        }
        Assert.assertEquals(expected, (int)container.compute(computation).get(5, TimeUnit.SECONDS).get());
        Assert.assertEquals(expected, (int)container.compute(computation).get().get());
    }

    @Test
    public void testInfiniteSeqFactorial() throws InterruptedException, ExecutionException, TimeoutException {
        ComputationContainer container = getManager().resolve(ComputationContainer.class, Default.class);
        Assert.assertNotNull(container);
        int n = 10;
        InfiniteSeqFactorialComputation computation = new InfiniteSeqFactorialComputation(n);
        int expected = 1;
        for (int i=2; i<=n; i++) {
            expected *= i;
        }
        Future<MergeableInteger> future = container.compute(computation);
        try {
            future.get(1, TimeUnit.SECONDS);
            Assert.fail(TimeoutException.class.getName() + " should be thrown.");
        } catch (TimeoutException ignored) {
        }
        Assert.assertEquals(expected, (int)future.getPartial().get());
    }


    private static class SeqFactorialComputation implements Computation<MergeableInteger> {

        private final Integer n;

        @Inject
        private Emitter emitter;

        public SeqFactorialComputation(Integer n) {
            this.n = n;
        }

        @Override
        public MergeableInteger call() throws Exception {
            if (n > 1) {
                emitter.emit(new SeqFactorialComputation(n - 1));
            }
            return new MergeableInteger(n);
        }

        @Override
        public void destroy() throws Exception {
        }

        @Override
        public String toString() {
            return getClass().getName() + " ### " + n;
        }

    }

    private static class ParFactorialComputation implements Computation<MergeableInteger> {

        private final Integer n;
        private final boolean cloned;

        @Inject
        private Emitter emitter;

        public ParFactorialComputation(Integer n) {
            this.n = n;
            this.cloned = false;
        }

        public ParFactorialComputation(Integer n, boolean cloned) {
            this.n = n;
            this.cloned = cloned;
        }

        @Override
        public MergeableInteger call() throws Exception {
            if (n > 1 && !cloned) {
                for (int i=1; i<n; i++) {
                    emitter.emit(new ParFactorialComputation(i, true));
                }
            }
            return new MergeableInteger(n);
        }

        @Override
        public void destroy() throws Exception {
        }

        @Override
        public String toString() {
            return getClass().getName() + " ### " + n;
        }

    }

    private static class InfiniteSeqFactorialComputation implements Computation<MergeableInteger> {

        private final Integer n;

        @Inject
        private Emitter emitter;

        public InfiniteSeqFactorialComputation(Integer n) {
            this.n = n;
        }

        @Override
        public MergeableInteger call() throws Exception {
            if (n > 1) {
                emitter.emit(new InfiniteSeqFactorialComputation(n - 1));
            } else {
                synchronized(this) {
                    wait();
                }
            }
            return new MergeableInteger(n);
        }

        @Override
        public void destroy() throws Exception {
        }

        @Override
        public String toString() {
            return getClass().getName() + " ### " + n;
        }

    }

    private static class MergeableInteger extends MergeableBox<MergeableInteger, Integer> {

        public MergeableInteger(Integer load) {
            super(load);
        }

        @Override
        public MergeableInteger merge(MergeableInteger toMerge) {
            return new MergeableInteger(get() * toMerge.get());
        }

        @Override
        public String toString() {
            return get().toString();
        }

    }

}
