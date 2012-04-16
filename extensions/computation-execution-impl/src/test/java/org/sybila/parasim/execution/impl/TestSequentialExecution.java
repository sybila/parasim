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

import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import org.sybila.parasim.core.ContextEvent;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.core.extension.cdi.api.ServiceFactory;
import org.sybila.parasim.execution.AbstractExecutionTest;
import org.sybila.parasim.execution.api.ComputationContext;
import org.sybila.parasim.execution.api.Execution;
import org.sybila.parasim.model.computation.Computation;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestSequentialExecution extends AbstractExecutionTest {
    
    @Test
    public void testExecute() throws InterruptedException, ExecutionException {
        Execution<MergeableString> execution = createSequentialExecution(new TestComputation("AHOJ", 0));
        assertEquals(execution.execute().get().getOriginal(), "0AHOJ");
    }

    @Test
    public void testAbort() throws InterruptedException, ExecutionException {
        Execution<MergeableString> execution = createSequentialExecution(new TestComputation("AHOJ", 10000));
        Future<MergeableString> result = execution.execute();
        result.cancel(true);
        try {
            result.get();
            fail("The execution has been canceled, so the Future.get() method should throw exception.");
        } catch(CancellationException e) {
        }
    }
    
    protected Execution<MergeableString> createSequentialExecution(Computation computation) {
        return SequentialExecution.of(
            getManager().resolve(java.util.concurrent.Executor.class, Default.class, getManager().getRootContext()),
            computation,
            getManager().resolve(ServiceFactory.class, Default.class, getManager().getRootContext()),
            new ContextEvent<ComputationContext>() {
                public void initialize(ComputationContext context) {
                    context.setParent(getManager().getRootContext());
                }
                public void finalize(ComputationContext context) {
                    context.destroy();
                }
            },
            0
        );        
    }

}
