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
import org.sybila.parasim.core.annotation.Qualifier;
import org.sybila.parasim.core.test.ParasimTest;
import org.testng.Assert;
import org.testng.annotations.Test;



public class TestDependencies extends ParasimTest {

    @Test
    public void testDependencyInOneExtension() {
        Assert.assertNotNull(getManager().resolve(String.class, Default.class));
        Assert.assertEquals(getManager().resolve(String.class, Default.class), DependencyExtension2.TO_RETURN);
    }

    @Test
    public void testDependencyInMoreExtensions() {
        Assert.assertNotNull(getManager().resolve(String.class, DependentQualifier.class));
        Assert.assertEquals(getManager().resolve(String.class, DependentQualifier.class), "dependent" + DependencyExtension2.TO_RETURN);
    }

    @Override
    protected Class<?>[] getExtensions() {
        return new Class<?>[] {DependencyExtension1.class, DependencyExtension2.class};
    }

}

class DependencyExtension1 {

    @Provide
    @DependentQualifier
    public String provideString(String string) throws Exception {
        return "dependent" + string;
    }

}

@Qualifier
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@interface DependentQualifier {}

class DependencyExtension2 {

    static final String TO_RETURN = "AHOJ";

    @Provide
    public String provideString(Callable callable) throws Exception {
        return callable.call().toString();
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