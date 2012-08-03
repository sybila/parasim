package org.sybila.parasim.extension.remote.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.extension.remote.api.RemoteHostControl;
import org.sybila.parasim.extension.remote.api.RemoteManager;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class RemoteHostControlImpl implements RemoteHostControl {

    private final URI host;
    private Process process;
    private RemoteManager remoteManager;
    private boolean running = false;
    private final File target;
    private final String username;

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteHostControlImpl.class);
    private static final String MAIN_CLASS = ParasimRemoteServer.class.getName();
    public static final String SECURITY_POLICY_PATH = RemoteControlImpl.class.getClassLoader().getResource("parasim.remote.security.policy").getFile();

    public RemoteHostControlImpl(URI host, File target) {
        Validate.notNull(host);
        Validate.notNull(target);
        this.host = host;
        this.target = target;
        this.username = null;
    }

    public RemoteHostControlImpl(URI host, String username, File target) {
        Validate.notNull(host);
        Validate.notNull(target);
        Validate.notNull(username);
        this.host = host;
        this.target = target;
        this.username = username;
    }

    @Override
    public URI getHost() {
        return host;
    }

    @Override
    public RemoteManager getManager() {
        if (remoteManager == null) {
            remoteManager = loadManager();
        }
        return remoteManager;
    }

    @Override
    public boolean isRunning(boolean ping) {
        if (ping) {
            running = ping();
        }
        return running;
    }

    @Override
    public <T extends Remote> T lookup(String name, Class<T> clazz) throws IOException {
        try {
            return (T) LocateRegistry.getRegistry(host.toString()).lookup(name);
        } catch(Exception e) {
            throw new IOException("Can't lookup the service <" + clazz.getName() + "> called <" + name + ">");
        }
    }

    @Override
    public void shutdown() {
        if (process == null) {
            throw new IllegalStateException("The host control is not running.");
        }
        process.destroy();
    }

    @Override
    public void start(long time, TimeUnit unit) throws IOException {
        if (isRunning(true)) {
            throw new IllegalStateException("The remote host control is already running.");
        }
        if (System.getSecurityManager() == null) {
            if (System.getProperty("java.security.policy") == null) {
                System.setProperty("java.security.policy", SECURITY_POLICY_PATH);
            }
            System.setSecurityManager(new RMISecurityManager());
        }
        RemoteProcessBuilder builder;
        if (username == null) {
            builder = new RemoteProcessBuilder(host);
        } else {
            builder = new RemoteProcessBuilder(host, username);
        }
        builder = builder.command("java -cp", target.getAbsolutePath());
        Enumeration<?> propertyNames = System.getProperties().propertyNames();
        while (propertyNames.hasMoreElements()) {
            String propertyName = (String) propertyNames.nextElement();
            if (propertyName.startsWith("parasim")) {
                builder = builder.command("-D" + propertyName + "=" + System.getProperty(propertyName));
            }
        }
        builder.command(MAIN_CLASS).command(host.toString()).spawn();
        running = true;
    }

    protected RemoteManager loadManager() {
        try {
            return lookup(MAIN_CLASS, RemoteManager.class);
        } catch (IOException e) {
            LOGGER.warn("The remote manager is reachable.", e);
            return null;
        }
    }

    protected boolean ping() {
        return loadManager() != null;
    }

}
