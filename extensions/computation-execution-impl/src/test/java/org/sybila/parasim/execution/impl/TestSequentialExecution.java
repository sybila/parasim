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
package org.sybila.parasim.execution.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.sybila.parasim.core.annotation.Default;
import org.sybila.parasim.core.api.Context;
import org.sybila.parasim.core.api.enrichment.Enrichment;
import org.sybila.parasim.execution.AbstractExecutionTest;
import org.sybila.parasim.execution.api.Execution;
import org.sybila.parasim.execution.api.annotations.ComputationScope;
import org.sybila.parasim.model.computation.Computation;
import org.sybila.parasim.model.computation.ComputationId;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestSequentialExecution extends AbstractExecutionTest {

    @Test
    public void testExecute() throws InterruptedException, ExecutionException, TimeoutException {
        super.testExecute(createSequentialExecution(new TestStringComputation("AHOJ", 0)), new MergeableString("0AHOJ"));
    }

    @Test
    public void testAbort() throws InterruptedException, ExecutionException, TimeoutException {
        super.testAbort(createSequentialExecution(new TestStringComputation("AHOJ", 10000)));
    }

    protected Execution<MergeableString> createSequentialExecution(Computation computation) {
        Context context = getManager().context(ComputationScope.class);
        return SequentialExecution.of(
                new ComputationId() {
                    @Override
                    public int currentId() {
                        return 0;
                    }

                    @Override
                    public int maxId() {
                        return 0;
                    }
                },
                getManager().resolve(java.util.concurrent.Executor.class, Default.class),
                computation,
                getManager().resolve(Enrichment.class, Default.class),
                context);
    }

}
