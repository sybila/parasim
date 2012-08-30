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
package org.sybila.parasim.core;

import org.sybila.parasim.core.context.Context;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.sybila.parasim.core.annotations.Scope;
import org.sybila.parasim.core.annotations.Provide;
import org.sybila.parasim.core.annotations.Inject;
import java.util.ArrayList;
import java.util.Collection;
import org.sybila.parasim.core.annotations.Any;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.core.annotations.Observes;
import org.sybila.parasim.core.annotations.Qualifier;
import org.sybila.parasim.core.context.AbstractContext;
import org.sybila.parasim.core.event.ManagerProcessing;
import org.sybila.parasim.core.event.ManagerStarted;
import org.sybila.parasim.core.event.ManagerStopping;
import org.sybila.parasim.core.spi.InstanceCleaner;
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
        Manager manager = ManagerImpl.create(TestedObservingExtension.class);
        manager.start();
        manager.shutdown();
        assertNotEquals((long) 0, managerProcessing, "The manager processing time is <" + managerProcessing + ">.");
        assertNotEquals((long) 0, managerStarted, "The manager started time is <" + managerStarted + ">.");
        assertNotEquals((long) 0, managerStopping, "The manager stopping time is <" + managerStopping + ">.");
        assertTrue(managerProcessing < managerStarted);
        assertTrue(managerStarted < managerStopping);
    }

    @Test
    public void testFieldProvider() throws Exception {
        Manager manager = ManagerImpl.create(TestedFieldProvidingExtension.class, TestedInjectingExtension.class);
        manager.start();
        assertNotNull(manager.resolve(Number.class, Default.class, manager.getRootContext()));
        for (int i=0; i<10; i++) {
            assertEquals(manager.resolve(Number.class, Default.class, manager.getRootContext()).get(), 0);
        }
    }

    @Test
    public void testFreshProvider() throws Exception {
        Manager manager = ManagerImpl.create(TestedFreshProvidingExtension.class, TestedInjectingExtension.class);
        manager.start();
        assertNotNull(manager.resolve(Number.class, Default.class, manager.getRootContext()));
        for (int i=0; i<10; i++) {
            assertEquals(manager.resolve(Number.class, Default.class, manager.getRootContext()).get(), i);
        }
    }

    @Test
    public void testStaticProvider() throws Exception {
        Manager manager = ManagerImpl.create(TestedStaticProvidingExtension.class, TestedInjectingExtension.class);
        manager.start();
        assertNotNull(manager.resolve(Number.class, Default.class, manager.getRootContext()));
        for (int i=0; i<10; i++) {
            assertEquals(manager.resolve(Number.class, Default.class, manager.getRootContext()).get(), 0);
        }
    }

    @Test
    public void testScopedStaticProvider() throws Exception {
        Manager manager = ManagerImpl.create(TestedScopedStaticProvidingExtension.class, TestedInjectingExtension.class);
        manager.start();
        assertNull(manager.resolve(Number.class, Default.class, manager.getRootContext()));
        Context testedContext = new TestContext();
        manager.initializeContext(testedContext);
        assertNotNull(manager.resolve(Number.class, Default.class, testedContext));
        for (int i=0; i<10; i++) {
            assertEquals(manager.resolve(Number.class, Default.class, testedContext).get(), 0);
        }
    }

    @Test
    public void testAmbigousResolving() throws Exception {
        ManagerImpl manager = (ManagerImpl) ManagerImpl.create();
        manager.start();
        manager.bind(String.class, Default.class, manager.getRootContext(), "default");
        manager.bind(String.class, MyQualifier.class, manager.getRootContext(), "my-qualifier");
        try {
            manager.resolve(String.class, Any.class, manager.getRootContext());
            fail("Exception hasn't been thrown when ambigous resolving happened.");
        } catch (AmbigousException e) {}
    }

    @Test
    public void testNonDefaultResolving() throws Exception {
        ManagerImpl manager = (ManagerImpl) ManagerImpl.create();
        manager.start();
        assertNull(manager.resolve(Float.class, Default.class, manager.getRootContext()));
        assertNull(manager.resolve(Float.class, MyQualifier.class, manager.getRootContext()));
        manager.bind(String.class, Default.class, manager.getRootContext(), "default");
        assertEquals(manager.resolve(String.class, MyQualifier.class, manager.getRootContext()), "default");
        manager.bind(String.class, MyQualifier.class, manager.getRootContext(), "my-qualifier");
        assertEquals(manager.resolve(String.class, Default.class, manager.getRootContext()), "default");
        assertEquals(manager.resolve(String.class, MyQualifier.class, manager.getRootContext()), "my-qualifier");
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

class TestedFreshProvidingExtension {

    private int counter = 0;

    @Provide(fresh=true)
    private Number getNumber() {
        final int x = counter++;
        return new Number() {

            public int get() {
                return x;
            }
        };
    }
}

class TestedStaticProvidingExtension {

    private int counter = 0;

    @Provide
    private Number getNumber() {
        final int x = counter++;
        return new Number() {

            public int get() {
                return x;
            }
        };
    }
}

class TestedFieldProvidingExtension {

    @Provide
    private Number number = new Number() {

        public int get() {
            return 0;
        }
    };

}

@TestScope
class TestedScopedStaticProvidingExtension {

    private int counter = 0;

    @Provide
    private Number getNumber() {
        final int x = counter++;
        return new Number() {

            public int get() {
                return x;
            }
        };
    }

}

interface Number {
    int get();
}

@Scope
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface TestScope {}



class TestContext extends AbstractContext {

    public TestContext() {
        super();
    }

    public TestContext(InstanceStorage instanceStorage) {
        super(instanceStorage);
    }

    public Class<? extends Annotation> getScope() {
        return TestScope.class;
    }
}

@Qualifier(parent=Default.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@interface MyQualifier {}