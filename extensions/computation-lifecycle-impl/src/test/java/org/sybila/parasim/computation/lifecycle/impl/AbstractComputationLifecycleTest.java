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
package org.sybila.parasim.computation.lifecycle.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import junit.framework.Assert;
import org.sybila.parasim.computation.lifecycle.api.Computation;
import org.sybila.parasim.computation.lifecycle.api.ComputationContainer;
import org.sybila.parasim.computation.lifecycle.api.Future;
import org.sybila.parasim.computation.lifecycle.test.MultiplicativeInteger;
import org.sybila.parasim.core.annotation.Default;
import org.sybila.parasim.core.test.RemoteParasimTest;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractComputationLifecycleTest extends RemoteParasimTest {

    protected void checkSeqFactorial(Computation<MultiplicativeInteger> computation, int n) throws InterruptedException, ExecutionException, TimeoutException {
        ComputationContainer container = getManager().resolve(ComputationContainer.class, Default.class);
        Assert.assertNotNull(container);
        int expected = factorial(n);
        MultiplicativeInteger result = container.compute(computation).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(result);
        Assert.assertEquals(expected, (int)result.get());
        Assert.assertEquals(expected, (int)container.compute(computation).get().get());
    }

    protected void checkParFactorial(Computation<MultiplicativeInteger> computation, int n) throws InterruptedException, ExecutionException, TimeoutException {
        ComputationContainer container = getManager().resolve(ComputationContainer.class, Default.class);
        Assert.assertNotNull(container);
        int expected = factorial(n);
        MultiplicativeInteger result = container.compute(computation).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(result);
        Assert.assertEquals(expected, (int)result.get());
        Assert.assertEquals(expected, (int)result.get());
    }

    protected void checkInfiniteSeqFactorial(Computation<MultiplicativeInteger> computation, int n) throws InterruptedException, ExecutionException, TimeoutException {
        ComputationContainer container = getManager().resolve(ComputationContainer.class, Default.class);
        Assert.assertNotNull(container);
        int expected = factorial(10);
        Future<MultiplicativeInteger> future = container.compute(computation);
        try {
            future.get(1, TimeUnit.SECONDS);
            Assert.fail(TimeoutException.class.getName() + " should be thrown.");
        } catch (TimeoutException ignored) {
        }
        MultiplicativeInteger result = future.getPartial();
        Assert.assertNotNull(result);
        Assert.assertFalse(future.isDone());
    }

    protected static int factorial(int n) {
        int result = 1;
        for (int i=2; i<=n; i++) {
            result *= i;
        }
        return result;
    }
}
