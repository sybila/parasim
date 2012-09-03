package org.sybila.parasim.extension.remote.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.concurrent.TimeUnit;
import junit.framework.Assert;
import org.sybila.parasim.core.annotations.Default;
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

    @Test
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
