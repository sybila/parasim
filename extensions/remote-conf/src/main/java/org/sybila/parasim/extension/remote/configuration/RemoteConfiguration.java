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
package org.sybila.parasim.extension.remote.configuration;

import java.io.File;
import java.rmi.registry.Registry;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.Validate;

/**
 * @author <a href="mailto:xpapous1@mail.muni.cz">Jan Papousek</a>
 */
public class RemoteConfiguration {

    private int port = Registry.REGISTRY_PORT;
    private long timeout = 1;
    private TimeUnit timeUnit = TimeUnit.MINUTES;
    private String username;
    private String[] hosts;
    private File target;

    public String[] getHosts() {
        return hosts;
    }

    public int getPort() {
        return port;
    }

    public File getTarget() {
        return target;
    }

    public long getTimeout() {
        return timeout;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public String getUsername() {
        return username;
    }

    public void validate() {
        Validate.isTrue(port > 0, "The port has to be a positive number, but it's <" + port + ">.");
    }

}
