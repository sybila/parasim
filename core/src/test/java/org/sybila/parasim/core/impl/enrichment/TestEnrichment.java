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
package org.sybila.parasim.core.impl.enrichment;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.sybila.parasim.core.annotation.Default;
import org.sybila.parasim.core.annotation.Inject;
import org.sybila.parasim.core.annotation.Provide;
import org.sybila.parasim.core.annotation.Qualifier;
import org.sybila.parasim.core.api.Binder;
import org.sybila.parasim.core.api.Context;
import org.sybila.parasim.core.api.enrichment.Enrichment;
import org.sybila.parasim.core.impl.enrichment.Number;
import org.sybila.parasim.core.spi.enrichment.Enricher;
import org.sybila.parasim.core.test.ParasimTest;
import static org.testng.Assert.*;
import org.testng.annotations.Test;


/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestEnrichment extends ParasimTest {

    @Inject
    private String toInject;
    @Inject
    private Number providedNumber;
    @Provide
    private Number2 toProvide = new Number2() {
        @Override
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
        @Override
        public int get() {
            return -10;
        }
    };
    public int counter = 0;

    public static int integerEnriched = 0;

    @Test
    public void testEnrichment() {
        ((Binder) getManager()).bind(String.class, Default.class, "HELLO");
        getManager().resolve(Enrichment.class, Default.class).enrich(this, getManager());
        assertEquals(toInject, "HELLO");
        assertEquals(getManager().resolve(Number2.class, Default.class).get(), toProvide.get());
        assertEquals(toInjectWithQualifier.get(), toProvideWithQualifier.get());
    }

    @Test
    public void testOwnEnricher() {
        int before = integerEnriched;
        Enrichment enrichment = getManager().resolve(Enrichment.class, Default.class);
        enrichment.enrich(new Integer(1), getManager());
        enrichment.enrich("1", getManager());
        assertEquals(integerEnriched, before+1);
    }

    @Override
    protected Map<Object, Class<?>> getServices() {
        Map<Object, Class<?>> services = new HashMap<>();
        services.put(new IntegerEnricher(), Enricher.class);
        return services;
    }

    @Provide
    private Number provider() {
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

interface Number2 {

    int get();
}

@Qualifier
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@interface TestQualifier {}

class IntegerEnricher implements Enricher<Integer> {

    @Override
    public void enrich(Integer target, Context context) {
        TestEnrichment.integerEnriched++;
    }

    @Override
    public void resolve(Method method, Object[] args, Context context) {
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

    @Override
    public int getPrecedence() {
        return 100;
    }

}