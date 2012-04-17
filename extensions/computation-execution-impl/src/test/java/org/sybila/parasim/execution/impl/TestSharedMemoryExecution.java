package org.sybila.parasim.execution.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.sybila.parasim.core.ContextEvent;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.core.extension.cdi.api.ServiceFactory;
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
            getManager().resolve(ServiceFactory.class, Default.class, getManager().getRootContext()),
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