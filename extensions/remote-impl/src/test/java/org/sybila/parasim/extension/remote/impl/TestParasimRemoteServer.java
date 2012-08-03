package org.sybila.parasim.extension.remote.impl;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.extension.remote.api.RemoteHostActivity;
import org.sybila.parasim.extension.remote.api.RemoteHostControl;
import org.sybila.parasim.extension.remote.api.RemoteManager;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestParasimRemoteServer {

    private static Thread mainThread;

    @BeforeClass
    public static void prepare() {
        System.setProperty("parasim.remote.timeout", "10");
        System.setProperty("parasim.remote.time.unit", "seconds");
        mainThread = new Thread("parasim-remote-server-thread") {
            @Override
            public void run() {
                try {
                    ParasimRemoteServer.main(new String[] {"127.0.0.1"});
                } catch(Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        };
        mainThread.start();
    }

    @AfterClass
    public static void clean() throws InterruptedException {
        if (mainThread != null) {
            mainThread.join();
        }
    }

    @Test
    public void testResolving() throws InterruptedException, RemoteException {
        RemoteManager manager = null;
        long expectedTimeout = System.currentTimeMillis() + 1000;
        while (System.currentTimeMillis() <= expectedTimeout) {
            try {
                manager = (RemoteManager) LocateRegistry.getRegistry().lookup(RemoteHostControl.REMOTE_MANAGER_CONTEXT);
            } catch(Exception e) {
                Thread.sleep(100);
            }
        }
        assertNotNull(manager);
        RemoteHostActivity activity = manager.resolve(RemoteHostActivity.class, Default.class);
        assertNotNull(activity);
    }

    @Test
    public void testManagerAvailable() throws InterruptedException, RemoteException, MalformedURLException, UnknownHostException {
        Thread.sleep(1000);
        RemoteManager manager = null;
        long expectedTimeout = System.currentTimeMillis() + 1000;
        while (System.currentTimeMillis() <= expectedTimeout) {
            try {
                manager = (RemoteManager) LocateRegistry.getRegistry("127.0.0.1").lookup(RemoteHostControl.REMOTE_MANAGER_CONTEXT);
            } catch(NotBoundException e) {
                Thread.sleep(100);
            }
        }
        assertNotNull(manager);
    }
}
