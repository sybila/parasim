package org.sybila.parasim.extension.remote.configuration;

import java.io.File;
import java.rmi.registry.Registry;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.Validate;

/**
 * @author <a href="mailto:xpapous1@mail.muni.cz">Jan Papousek</a>
 */
public class RemoteConfiguration {

    private File hostFile;

    private int port = Registry.REGISTRY_PORT;
    private long timeout = 1;
    private TimeUnit timeUnit = TimeUnit.MINUTES;

    public File getHostFile() {
        return hostFile;
    }

    public int getPort() {
        return port;
    }

    public long getTimeout() {
        return timeout;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void validate() {
//        Validate.notNull(hostFile);
        Validate.isTrue(port > 0, "The port has to be a positive number, but it's <" + port + ">.");
    }

}
