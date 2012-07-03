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
package org.sybila.parasim.core.extension.interceptor;

import java.lang.reflect.Method;
import org.sybila.parasim.core.extension.interceptor.api.Invocation;
import org.sybila.parasim.core.service.Interceptor;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.core.extension.AbstractExtensionTest;
import org.sybila.parasim.core.extension.interceptor.api.InterceptorRegistry;
import org.sybila.parasim.core.extension.loader.TestedLoadableExtension;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestInterceptorExtension extends AbstractExtensionTest{

    private static int sum = 0;

    @Test
    public void testLoad() {
        getManager().start();
        assertNotNull(getManager().resolve(InterceptorRegistry.class, Default.class, getManager().getRootContext()));
    }

    @Test
    public void testInterceptor() {
        sum = 0;
        getManager().start();
        TestedLoadableExtension.IntegerBox box = getManager().resolve(TestedLoadableExtension.IntegerBox.class, Default.class, getManager().getRootContext());
        for (int i=1; i<=10; i++) {
            box.getInside();
            assertEquals(sum, i);
        }
    }

    public static class TestInterceptor implements Interceptor {

        @Override
        public Object intercept(Object obj, Method method, Object[] args, Invocation invocation) throws Throwable {
            Object result = invocation.invoke(obj, method, args);
            if (result instanceof Integer) {
                sum += (Integer) result;
            }
            return result;
        }

    }

}
