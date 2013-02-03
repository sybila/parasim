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
package org.sybila.parasim.extension.remote.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.concurrent.TimeUnit;
import junit.framework.Assert;
import org.sybila.parasim.core.annotation.Default;
import org.sybila.parasim.extension.remote.api.RemoteHostActivity;
import org.sybila.parasim.extension.remote.api.RemoteHostControl;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestRemoteHostControlImpl {

    private static RemoteHostControl control;

    private static class TestedRemoteControlImpl extends RemoteHostControlImpl {

        public TestedRemoteControlImpl(URI host) {
            super(host, new File("/tmp"));
        }

        @Override
        public void start(long time, TimeUnit unit) throws IOException {
            try {
                System.setProperty("parasim.remote.timeout", Long.toString(time));
                System.setProperty("parasim.remote.time.unit", unit.name());
                ParasimRemoteServer.main(new String[] {getHost().toString()});
            } catch (Exception e) {
                throw new IOException(e);
            }
        }

        @Override
        public void shutdown() {
        }
    }

    @BeforeClass
    public static void prepare() throws Exception {
        control = new TestedRemoteControlImpl(new URI("127.0.0.1"));
        control.start(10, TimeUnit.SECONDS);
    }

    @Test(enabled=false)
    public void testResolving() throws InterruptedException, RemoteException {
        Assert.assertNotNull(control.getManager().resolve(RemoteHostActivity.class, Default.class));
    }

    @Test
    public void testIsRunning() {
        Assert.assertTrue(control.isRunning(true));
    }

    @Test
    public void testManagerAvailable() throws URISyntaxException, IOException {
        Assert.assertNotNull(control.getManager());
    }

}
