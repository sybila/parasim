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
package org.sybila.parasim.core.impl;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import org.sybila.parasim.core.annotation.Any;
import org.sybila.parasim.core.annotation.Default;
import org.sybila.parasim.core.annotation.Provide;
import org.sybila.parasim.core.annotation.Qualifier;
import org.sybila.parasim.core.annotation.Scope;
import org.sybila.parasim.core.api.Binder;
import org.sybila.parasim.core.api.Context;
import org.sybila.parasim.core.common.AmbigousException;
import org.sybila.parasim.core.event.ManagerStarted;
import org.sybila.parasim.core.event.ManagerStopping;
import org.sybila.parasim.core.impl.Number;
import org.sybila.parasim.core.test.ParasimTest;
import static org.testng.Assert.*;
import org.testng.annotations.Test;


/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestManagerImpl extends ParasimTest {

    @Test
    public void testLifeCycle() throws Exception {
        assertEquals(getNumberOfEvents(ManagerStarted.class), 1);
        assertEquals(getNumberOfEvents(ManagerStopping.class), 0);
        getManager().destroy();
        assertEquals(getNumberOfEvents(ManagerStopping.class), 1);
    }

    @Test
    public void testCallableServices() throws Exception {
        assertNotEquals(0, getManager().service(Callable.class).size());
        for (Callable callable: getManager().service(Callable.class)) {
            callable.call();
        }
    }

    @Test
    public void testFieldProvider() throws Exception {
        assertNotNull(getManager().resolve(Number.class, Field.class));
        assertNotNull(getManager().resolve(Integer.class, Field.class));
        for (int i=0; i<10; i++) {
            assertEquals(getManager().resolve(Number.class, Field.class).get(), 0);
            assertEquals(getManager().resolve(Integer.class, Field.class), new Integer(0));
        }
    }

    @Test
    public void testMethodProvider() throws Exception {
        assertNotNull(getManager().resolve(Number.class, Method.class));
        for (int i=0; i<10; i++) {
            assertEquals(getManager().resolve(Number.class, Method.class).get(), 0);
        }
    }

    @Test
    public void testScopedMethodProvider() throws Exception {
        assertNotNull(getManager().resolve(Number.class, Method.class));
        assertEquals(getManager().resolve(Number.class, Method.class).get(), 0);
        Context testContext = getManager().context(TestScope.class);
        assertNotNull(testContext.resolve(Number.class, Method.class));
        for (int i=0; i<10; i++) {
            assertEquals(testContext.resolve(Number.class, Method.class).get(), 100);
        }
    }

    @Test
    public void testAmbigousResolving() throws Exception {
        Binder binder = (Binder) getManager();
        binder.bind(String.class, Default.class, "default");
        binder.bind(String.class, MyQualifier.class, "my-qualifier");
        try {
            getManager().resolve(String.class, Any.class);
            fail("Exception hasn't been thrown when ambigous resolving happened.");
        } catch (AmbigousException e) {}
    }

    @Override
    protected Class<?>[] getExtensions() {
        return new Class<?>[] {
            TestedFieldProvidingExtension.class,
            TestedMethodProvidingExtension.class,
            TestedScopedMethodProvidingExtension.class
        };
    }

    @Override
    protected Map<Object, Class<?>> getServices() {
        Map<Object, Class<?>> services = new HashMap<>();
        services.put(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return null;
            }
        }, Callable.class);
        return services;
    }
}

class TestedMethodProvidingExtension {

    private int counter = 0;

    @Provide
    @Method
    private Number getNumber() {
        final int x = counter++;
        return new Number() {

            @Override
            public int get() {
                return x;
            }
        };
    }
}

class TestedFieldProvidingExtension {

    @Provide
    @Field
    private Number number = new Number() {

        @Override
        public int get() {
            return 0;
        }
    };

    @Provide
    @Field
    private Integer integer = 0;

}


@TestScope
class TestedScopedMethodProvidingExtension {

    private int counter = 100;

    @Provide
    @Method
    private Number getNumber() {
        final int x = counter++;
        return new Number() {

            @Override
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

@Qualifier(parent=Default.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@interface MyQualifier {}

@Qualifier(parent=Default.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@interface Method {}

@Qualifier(parent=Default.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@interface Field {}