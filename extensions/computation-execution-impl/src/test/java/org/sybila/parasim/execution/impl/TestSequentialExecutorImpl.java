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

import java.util.concurrent.TimeoutException;
import org.sybila.parasim.execution.api.SequentialExecutor;
import org.sybila.parasim.model.computation.Computation;
import java.util.concurrent.ExecutionException;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.execution.AbstractExecutionTest;
import org.sybila.parasim.execution.api.Execution;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestSequentialExecutorImpl extends AbstractExecutionTest {

    @Test
    public void testExecute() throws InterruptedException, ExecutionException, TimeoutException {
        super.testExecute(createSequentialExecution(new TestStringComputation("AHOJ", 0)), new MergeableString("0AHOJ"));
    }

    @Test
    public void testAbort() throws InterruptedException, ExecutionException, TimeoutException {
        super.testAbort(createSequentialExecution(new TestStringComputation("AHOJ", 10000)));
    }

    protected Execution<MergeableString> createSequentialExecution(Computation computation) {
        return getManager().resolve(SequentialExecutor.class, Default.class, getManager().getRootContext()).submit(computation);
    }
}