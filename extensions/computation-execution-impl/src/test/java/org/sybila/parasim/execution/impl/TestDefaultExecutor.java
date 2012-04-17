package org.sybila.parasim.execution.impl;

import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.execution.AbstractExecutionTest;
import org.sybila.parasim.execution.api.SharedMemoryExecutor;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestDefaultExecutor extends AbstractExecutionTest {

    @Test
    public void testDefaultExecutorIsLoaded() {
        assertNotNull(getManager().resolve(SharedMemoryExecutor.class, Default.class, getManager().getRootContext()));
    }

}
