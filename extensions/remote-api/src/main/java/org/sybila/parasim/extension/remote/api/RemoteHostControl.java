package org.sybila.parasim.extension.remote.api;

import java.io.IOException;
import java.net.URI;
import java.rmi.Remote;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:xpapous1@mail.muni.cz">Jan Papousek</a>
 */
public interface RemoteHostControl {

    public static final String REMOTE_MANAGER_CONTEXT = "parasimRemoteManager";

    URI getHost();

    RemoteManager getManager();

    boolean isRunning(boolean ping);

    <T extends Remote> T lookup(String name, Class<T> clazz) throws IOException;

    void shutdown();

    void start(long timeout, TimeUnit unit) throws IOException;
}
