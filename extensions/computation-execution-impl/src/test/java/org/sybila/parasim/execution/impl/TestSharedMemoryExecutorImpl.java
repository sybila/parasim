package org.sybila.parasim.execution.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.execution.AbstractExecutionTest;
import org.sybila.parasim.execution.api.Execution;
import org.sybila.parasim.execution.api.SharedMemoryExecutor;
import org.sybila.parasim.execution.conf.ExecutionConfiguration;
import org.sybila.parasim.model.computation.Computation;
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

    @Test
    public void testAbort() throws InterruptedException, ExecutionException, TimeoutException {
        super.testAbort(createSharedMemoryExecution(new TestIntegerComputation(100000)));
    }

    protected Execution<MergeableInteger> createSharedMemoryExecution(Computation computation) {
        return getManager().resolve(SharedMemoryExecutor.class, Default.class, getManager().getRootContext()).execute(computation);
    }

    protected MergeableInteger createExpectedResult() {
        int expected = 0;
        for (int i=1; i<getManager().resolve(ExecutionConfiguration.class, Default.class, getManager().getRootContext()).getNumberOfThreadsInSharedMemory(); i++) {
            expected += i;
        }
        return new MergeableInteger(expected);
    }
}
