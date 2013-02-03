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
import java.util.concurrent.Callable;
import org.sybila.parasim.core.annotation.Default;
import org.sybila.parasim.core.annotation.Provide;
import org.sybila.parasim.core.annotation.Scope;
import org.sybila.parasim.core.test.ParasimTest;
import org.testng.Assert;
import org.testng.annotations.Test;


public class TestScopes extends ParasimTest {

    @Test
    public void testOneScopeOnly() {
        Assert.assertNotNull(getManager().context(Test1Scope.class).context(Test2Scope.class).resolve(Callable.class, Default.class));
    }

    @Test
    public void testMoreScopes() {
        Assert.assertEquals(getManager().context(Test1Scope.class).context(Test2Scope.class).resolve(String.class, Default.class), Test2Extension.TO_RETURN);
    }

    @Override
    protected Class<?>[] getExtensions() {
        return new Class<?>[] {Test2Extension.class};
    }

}
@Scope
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface Test1Scope {}

@Scope
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface Test2Scope {}

class Test1Extension {

    public static final String TO_RETURN = "AHOJ1";

    @Provide
    public String povideString() {
        return TO_RETURN;
    }
}

@Test2Scope
class Test2Extension {

    public static final String TO_RETURN = "AHOJ2";

    @Provide
    public String povideString() {
        return TO_RETURN;
    }


    @Provide
    public Callable provideCallable() {
        return new Callable() {
            @Override
            public Object call() throws Exception {
                return TO_RETURN;
            }
        };
    }

}
