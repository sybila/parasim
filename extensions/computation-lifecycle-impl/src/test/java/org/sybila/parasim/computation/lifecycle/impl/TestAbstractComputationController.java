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
package org.sybila.parasim.computation.lifecycle.impl;

import java.lang.annotation.Annotation;
import org.sybila.parasim.core.InstanceStorage;
import org.sybila.parasim.core.extension.cdi.api.ServiceFactory;
import org.sybila.parasim.computation.lifecycle.api.ComputationContainer;
import org.sybila.parasim.computation.lifecycle.api.ComputationStatus;
import org.sybila.parasim.computation.lifecycle.api.annotations.After;
import org.sybila.parasim.computation.lifecycle.api.annotations.Before;
import org.sybila.parasim.computation.lifecycle.api.annotations.Start;
import org.sybila.parasim.computation.lifecycle.api.annotations.Stop;
import org.sybila.parasim.core.ContextEvent;
import org.sybila.parasim.core.context.Context;
import org.sybila.parasim.core.extension.cdi.impl.AbstractServiceFactory;
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
        ServiceFactory serviceFactory = new AbstractServiceFactory() {

            public <T> T getService(Class<T> type, Context context) {
                if (type.equals(String.class)) {
                    return type.cast(TO_INJECT);
                } else {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            }

            public boolean isServiceAvailable(Class<?> type, Context context) {
                if (type.equals(String.class)) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            protected <T> void bind(Class<T> clazz, Context context, Object value) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        ContextEvent<ComputationContext> contextEvent = new ContextEvent<ComputationContext>() {
            public void initialize(ComputationContext context) {}

            public void finalize(ComputationContext context) {}
        };
        container = new DefaultComputationContainer(serviceFactory, contextEvent);
        computation = new ComputationMock();
    }
    
    @Test
    public void testLifeCycle() {
        computation.controller = new AbstractComputationController() {
            
            @After
            public void destroy() {
                status.setFinished();
            }
            
            @Before
            public void init() {}
            
            @Start(controlsLifeCycle=true)
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
