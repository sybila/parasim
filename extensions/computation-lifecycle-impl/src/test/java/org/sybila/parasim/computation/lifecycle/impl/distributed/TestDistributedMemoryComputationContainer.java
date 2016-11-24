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
package org.sybila.parasim.computation.lifecycle.impl.distributed;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.sybila.parasim.computation.lifecycle.api.DistributedMemoryExecutor;
import org.sybila.parasim.computation.lifecycle.impl.AbstractComputationLifecycleTest;
import org.sybila.parasim.computation.lifecycle.test.InfiniteSequentialFactorial;
import org.sybila.parasim.computation.lifecycle.test.ParallelFactorial;
import org.sybila.parasim.computation.lifecycle.test.SequentialFactorial;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestDistributedMemoryComputationContainer extends AbstractComputationLifecycleTest{

    @Test(enabled = false)
    public void testSeqFactorial() throws InterruptedException, ExecutionException, TimeoutException {
        checkSeqFactorial(new SequentialFactorial(10), 10);
    }

    @Test(enabled = false)
    public void testParFactorial() throws InterruptedException, ExecutionException, TimeoutException {
        checkParFactorial(new ParallelFactorial(10), 10);
    }

    @Test(enabled = false)
    public void testInfiniteSeqFactorial() throws InterruptedException, ExecutionException, TimeoutException {
        checkInfiniteSeqFactorial(new InfiniteSequentialFactorial(10), 10);
    }

    @Override
    protected void beforeManagerCreated() {
        super.beforeManagerCreated();
        System.setProperty("parasim.computation.lifecycle.default.executor", DistributedMemoryExecutor.class.getName());
    }

}
