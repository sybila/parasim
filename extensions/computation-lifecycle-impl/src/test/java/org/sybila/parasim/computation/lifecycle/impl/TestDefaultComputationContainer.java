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
import org.sybila.parasim.core.ContextEvent;
import org.sybila.parasim.core.context.Context;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.extension.cdi.impl.AbstractServiceFactory;
import org.sybila.parasim.core.extension.cdi.api.ServiceFactory;
import org.sybila.parasim.computation.lifecycle.api.ComputationContainer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sybila.parasim.computation.lifecycle.api.annotations.Before;
import org.sybila.parasim.computation.lifecycle.api.annotations.Start;
import org.sybila.parasim.computation.lifecycle.api.annotations.ThreadId;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestDefaultComputationContainer extends AbstractComputationTest {
    
    private static final String TO_INJECT = "INJECTED";
    
    private ServiceFactory serviceFactory;
    private ComputationMock computation;
    private ComputationContainer container;
    private String monkey;
    
    @BeforeMethod
    public void setUp() {
        serviceFactory = new AbstractServiceFactory() {
            public <T> T getService(Class<T> type, Context context, Class<? extends Annotation> qualifier) {
                if (type.equals(String.class)) {
                    return type.cast(TO_INJECT);
                } else {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            }

            public boolean isServiceAvailable(Class<?> type, Context context, Class<? extends Annotation> qualifier) {
                if (type.equals(String.class)) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            protected <T> void bind(Class<T> clazz, Class<? extends Annotation> qualifier, Context context, Object value) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public <T> T getService(Class<T> type, Context context) {
                return getService(type, context, null);
            }

            public boolean isServiceAvailable(Class<?> type, Context context) {
                return isServiceAvailable(type, context, null);
            }
        };
        ContextEvent<ComputationContext> contextEvent = new ContextEvent<ComputationContext>() {
            public void initialize(ComputationContext context) {}

            public void finalize(ComputationContext context) {}
        };
        container = new DefaultComputationContainer(serviceFactory, contextEvent);
        computation = new ComputationMock();
    }
    
    @AfterMethod
    public void tearDown() {
        monkey = null;
    }
    
    @Test
    public void testInitialize() {
        computation.controller = new AbstractComputationController() {
            @Inject
            private String toInject;
            
            @Before
            public void init() {
                monkey = toInject;
            }
        };
        container.init(computation);
        assertEquals(monkey, TO_INJECT);
    }
    
    @Test
    public void testInitializeWithParams() {
        computation.controller = new AbstractComputationController() {
            
            @Before
            public void init(String toInject) {
                monkey = toInject;
            }
        };
        container.init(computation);
        assertEquals(monkey, TO_INJECT);
    }
    
    @Test
    public void testStart() {
        computation.controller = new AbstractComputationController() {
           
            @Start
            public void second() {
                monkey += " and second";
            }
            
            @Start
            public void first() {
                monkey = "first";
            }
           
            @Start
            public void markAsRunning() {
                status.startRunning();
            }
            
        };
        container.start(computation);
        assertEquals(monkey, "first and second");
    }
 
    @Test
    public void testStartInOwnThreadDefaultNumberOfThreads() throws Exception {
        computation.controller = new AbstractComputationController() {
            
            @Start(ownThread=true)
            public void startInNewThread() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(TestDefaultComputationContainer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        container.start(computation);
        assertTrue(computation.getController().getStatus().isRunning());
        Thread.sleep(1500);
        assertFalse(computation.getController().getStatus().isRunning());
    }
    
    @Test
    public void testStartInOwnThreadAutoNumberOfThreads() {
        final AtomicInteger numberOfThreads = new AtomicInteger(0);
        computation.controller = new AbstractComputationController() {
            @Start(ownThread=true, numberOfThreads=0)
            public void startInNewThread() {
                numberOfThreads.incrementAndGet();
            }
        };
        container.start(computation);
        while (computation.getController().getStatus().isRunning()) {}
        assertEquals(numberOfThreads.get(), Runtime.getRuntime().availableProcessors());
    }
    
    @Test
    public void testStartInOwnThreadSpecifiedNumberOfThreads() {
        final AtomicInteger numberOfThreads = new AtomicInteger(0);
        computation.controller = new AbstractComputationController() {
            @Start(ownThread=true, numberOfThreads=10)
            public void startInNewThread() {
                numberOfThreads.incrementAndGet();
            }
        };
        container.start(computation);
        while (computation.getController().getStatus().isRunning()) {}
        assertEquals(numberOfThreads.get(), 10);
    }
    
    @Test
    public void testStartInOwnThreadAndInjectThreadId() {
        final AtomicInteger sumOfIds = new AtomicInteger(0);
        computation.controller = new AbstractComputationController() {
            @Start(ownThread=true, numberOfThreads=4)
            public void startInNewThreadWithThreadId(@ThreadId int threadId, @ThreadId int threadId2) {
                sumOfIds.addAndGet(threadId);
                sumOfIds.addAndGet(threadId2);
            }
        };
        container.start(computation);
        while (computation.getController().getStatus().isRunning()) {}
        assertEquals(sumOfIds.get(), 12);
    }
}