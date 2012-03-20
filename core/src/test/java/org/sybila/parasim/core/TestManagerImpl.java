package org.sybila.parasim.core;

import org.sybila.parasim.core.annotations.Inject;
import java.util.ArrayList;
import java.util.Collection;
import org.sybila.parasim.core.annotations.Observes;
import org.sybila.parasim.core.event.ManagerProcessing;
import org.sybila.parasim.core.event.ManagerStarted;
import org.sybila.parasim.core.event.ManagerStopping;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestManagerImpl {

    static long managerProcessing;
    static long managerStarted;
    static long managerStopping;

    @BeforeMethod 
    public void resetLifeCycle() {
        managerProcessing = 0;
        managerStarted = 0;
        managerStopping = 0;
    }
    
    @Test
    public void testLifeCycle() throws Exception {
        Collection<Class<?>> extensions = new ArrayList<Class<?>>();
        extensions.add(TestedObservingExtension.class);
        Manager manager = ManagerImpl.create(extensions);
        manager.start();
        manager.shutdown();
        assertNotEquals((long) 0, managerProcessing, "The manager processing time is <" + managerProcessing + ">.");
        assertNotEquals((long) 0, managerStarted, "The manager started time is <" + managerStarted + ">.");
        assertNotEquals((long) 0, managerStopping, "The manager stopping time is <" + managerStopping + ">.");
        assertTrue(managerProcessing < managerStarted);
        assertTrue(managerStarted < managerStopping);
    }
}

class TestedObservingExtension {

    @Inject
    private Instance<String> inject;
    
    public void observesProcessing(@Observes ManagerProcessing event) throws InterruptedException {
        TestManagerImpl.managerProcessing = System.currentTimeMillis();
        Thread.sleep(50);
    }

    public void observesStarted(@Observes ManagerStarted event) throws InterruptedException {
        TestManagerImpl.managerStarted = System.currentTimeMillis();
        Thread.sleep(50);
    }
     
    public void observesStopping(@Observes ManagerStopping event) throws InterruptedException {
        TestManagerImpl.managerStopping = System.currentTimeMillis();
        Thread.sleep(50);
    }
    
    public void observesString(@Observes String event) {
        assertNotNull(inject);
        assertEquals(event, "FIRED");
        assertEquals(inject.get(), "INJECTED");
    }
}

class TestedInjectingExtension {
    
    @Inject
    private Event<String> event;
    @Inject
    private Instance<String> inject;
    
    public void observes(@Observes ManagerStarted event) {
        inject.set("INJECTED");
        this.event.fire("FIRED");
    }
    
}