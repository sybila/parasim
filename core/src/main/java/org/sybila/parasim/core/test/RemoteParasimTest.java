/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.core.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import org.sybila.parasim.core.annotation.Default;
import org.sybila.parasim.core.api.remote.HostControl;
import org.sybila.parasim.core.api.remote.Loader;
import org.sybila.parasim.core.impl.remote.HostControlImpl;
import org.testng.annotations.BeforeMethod;

public class RemoteParasimTest extends ParasimTest {

    @BeforeMethod(dependsOnMethods={"startManager"})
    protected void startServer() throws RemoteException {
        getManager().resolve(Loader.class, Default.class).load(Loader.class, Default.class);
    }

    protected HostControl getRemoteControl() throws URISyntaxException {
        return new HostControlImpl(new URI("127.0.0.1"));
    }

}
