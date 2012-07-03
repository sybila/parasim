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
package org.sybila.parasim.core.extension.cdi;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.sybila.parasim.core.annotations.Qualifier;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.core.annotations.Provide;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.extension.AbstractExtensionTest;
import org.sybila.parasim.core.extension.cdi.api.ServiceFactory;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestServiceFactoryExtension extends AbstractExtensionTest {

    @Inject
    private String toInject;
    @Inject
    private Number providedNumber;
    @Provide
    private Number2 toProvide = new Number2() {
        public int get() {
            return 10;
        }
    };
    @TestQualifier
    @Inject
    private Number toInjectWithQualifier;
    @TestQualifier
    @Provide
    private Number toProvideWithQualifier = new Number() {
        public int get() {
            return -10;
        }
    };
    public static ServiceFactory serviceFactory;
    public int counter = 0;

    @Test
    public void testServiceFactory() {
        getManager().start();
        assertNotNull(serviceFactory);
        serviceFactory.provideFieldsAndMethods(this, getManager().getRootContext());
        serviceFactory.injectFields(this, getManager().getRootContext());
        assertEquals(toInject, "HELLO");
        assertEquals(getManager().resolve(Number2.class, Default.class, getManager().getRootContext()).get(), toProvide.get());
        assertEquals(toInjectWithQualifier.get(), toProvideWithQualifier.get());
    }

    @Test
    public void testServiceFactoryWithFreshProvider() {
        getManager().start();
        serviceFactory.provideFieldsAndMethods(this, getManager().getRootContext());
        serviceFactory.injectFields(this, getManager().getRootContext());
        for (int i=0; i<10; i++) {
            assertEquals(providedNumber.get(), i);
        }
    }

    @Provide(fresh=true)
    private Number provider() {
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

interface Number2 {

    int get();
}

@Qualifier
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@interface TestQualifier {}