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
package org.sybila.parasim.core.impl.remote;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import org.sybila.parasim.core.annotation.Default;
import org.sybila.parasim.core.annotation.Provide;
import org.sybila.parasim.core.api.remote.HostControl;
import org.sybila.parasim.core.api.remote.Loader;
import org.sybila.parasim.core.test.ParasimTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestHostControlImpl extends ParasimTest {

    @BeforeMethod(dependsOnMethods={"startManager"})
    public void startServer() throws RemoteException {
        new LoaderImpl(getManager()).load(Loader.class, Default.class);
    }

    @Test
    public void testIsRunningOnLocalhost() throws URISyntaxException {
        HostControl hostControl = new HostControlImpl(new URI("localhost"));
        Assert.assertTrue(hostControl.isRunning(true));
    }

    @Test
    public void testIsRunningOnLocalIP() throws URISyntaxException {
        HostControl hostControl = new HostControlImpl(new URI("127.0.0.1"));
        Assert.assertTrue(hostControl.isRunning(true));
    }

    @Test
    public void testCounter() throws URISyntaxException, IOException {
        HostControl hostControl = new HostControlImpl(new URI("127.0.0.1"));
        for (int i=0; i<10; i++) {
            Assert.assertEquals(hostControl.lookup(TestCounter.class, Default.class).count(), i);
        }
    }

    @Override
    protected Class<?>[] getExtensions() {
        return new Class<?>[] {
            TestExtension.class
        };
    }

    public static class TestExtension {

        @Provide(immediately=true)
        public TestCounter provideCounter() {
            return new TestCounterImpl();
        }

    }

}
