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
package org.sybila.parasim.core.extension.interceptor.impl;

import java.lang.reflect.Method;
import org.sybila.parasim.core.extension.interceptor.api.Intercepted;
import org.sybila.parasim.core.extension.interceptor.api.InterceptionException;
import org.sybila.parasim.core.spi.Interceptor;
import org.sybila.parasim.core.extension.interceptor.api.Invocation;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestStandaloneIntercepted {

    private int counter = 0;

    private static class A {

        private String name = "test";

        public A() {
        }

        public A(String name) {
            this.name = name;
        }

        @Deprecated
        public String getName() {
            return name;
        }

        public String getName2Times() {
            return name+name;
        }
    }

    @BeforeMethod
    public void resetCounter() {
        counter = 0;
    }

    @Test
    public void testInterceptAnnotation() throws InterceptionException {
        Intercepted<A> intercepted = new StandaloneIntercepted<>(new A("blahblah"));
        intercepted.intercept(new Interceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, Invocation invocation) throws Throwable {
                counter++;
                return invocation.invoke(obj, method, args);
            }
        }, Deprecated.class);
        A proxy = intercepted.getProxyObject();
        String result = proxy.getName();
        assertEquals(counter, 1);
        proxy.getName2Times();
        assertEquals(result, "blahblah");
        assertEquals(counter, 1);
    }

    @Test
    public void testInterceptSomeMethods() throws InterceptionException, NoSuchMethodException {
        Intercepted<A> intercepted = new StandaloneIntercepted<>(new A("blahblah"));
        intercepted.intercept(new Interceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, Invocation invocation) throws Throwable {
                counter++;
                return invocation.invoke(obj, method, args);
            }
        }, A.class.getDeclaredMethod("getName", new Class<?>[0]));
        A proxy = intercepted.getProxyObject();
        String result = proxy.getName();
        assertEquals(counter, 1);
        proxy.getName2Times();
        assertEquals(result, "blahblah");
        assertEquals(counter, 1);
    }

    @Test
    public void testInterceptAllMethods() throws InterceptionException {
        Intercepted<A> intercepted = new StandaloneIntercepted<>(new A("blahblah"));
        intercepted.intercept(new Interceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, Invocation invocation) throws Throwable {
                counter++;
                return invocation.invoke(obj, method, args);
            }
        });
        String result = intercepted.getProxyObject().getName();
        assertEquals(result, "blahblah");
        assertEquals(counter, 1);
    }

}
