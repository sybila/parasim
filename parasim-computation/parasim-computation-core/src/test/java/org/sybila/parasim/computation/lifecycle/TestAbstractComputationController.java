package org.sybila.parasim.computation.lifecycle;

import org.sybila.parasim.computation.lifecycle.annotations.After;
import org.sybila.parasim.computation.lifecycle.annotations.Before;
import org.sybila.parasim.computation.lifecycle.annotations.Start;
import org.sybila.parasim.computation.lifecycle.annotations.Stop;
import org.sybila.parasim.model.cdi.MapServiceFactory;
import org.sybila.parasim.model.cdi.ServiceFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestAbstractComputationController extends AbstractComputationTest {
    
    private static final String TO_INJECT = "INJECTED";
    private ComputationMock computation;
    private ComputationContainer container;
    
    @BeforeMethod
    public void setUp() {
        ServiceFactory serviceFactory = new MapServiceFactory();
        serviceFactory.addService(String.class, TO_INJECT);
        container = new DefaultComputationContainer(serviceFactory);
        computation = new ComputationMock();
    }
    
    @Test
    public void testLifeCycle() {
        computation.controller = new AbstractComputationController() {
            
            @After
            public void destroy() {
                status.setFinished();
                status.setFinalized();
            }
            
            @Before
            public void init() {
                status.setInitialized();
            }
            
            @Start
            public void start() {
                status.startRunning();
            }
            
            @Stop
            public void stop() {
                status.stopRunning();
            }
        };
        container.init(computation);
        assertStatus(computation.getController().getStatus(), false, false, true, false, false);
        container.start(computation);
        assertStatus(computation.getController().getStatus(), false, false, true, true, true);
        container.stop(computation);
        assertStatus(computation.getController().getStatus(), false, false, true, false, true);
        container.finalize(computation);
        assertStatus(computation.getController().getStatus(), true, true, true, false, true);
    }
    
    @Test
    public void testConsumedTime() throws InterruptedException {
        computation.controller = new AbstractComputationController() {
            @Start
            public void start() {
                status.startRunning();
            }
            
            @Stop
            public void stop() {
                status.stopRunning();
            }
        };
        assertEquals(0, computation.getController().getStatus().getLastConsumedTime());
        assertEquals(0, computation.getController().getStatus().getTotalConsumedTime());
        container.start(computation);
        try {
            computation.getController().getStatus().getLastConsumedTime();
            fail();
        } catch (IllegalStateException e) {}
        container.stop(computation);
        assertNotEquals(this, computation.getController().getStatus().getLastConsumedTime());
        assertNotEquals(this, computation.getController().getStatus().getTotalConsumedTime());
        long before = computation.getController().getStatus().getTotalConsumedTime();
        container.start(computation);
        Thread.sleep(1000);
        container.stop(computation);
        assertTrue(before < computation.getController().getStatus().getTotalConsumedTime());
    }
    
    private void assertStatus(ComputationStatus status, boolean finalized, boolean finished, boolean initialized, boolean running, boolean started) {
        assertEquals(status.isFinalized(), finalized);
        assertEquals(status.isFinished(), finished);
        assertEquals(status.isInitialized(), initialized);
        assertEquals(status.isRunning(), running);
        assertEquals(status.isStarted(), started);
    }
    
}
