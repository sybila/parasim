/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import org.testng.annotations.Test;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestRemoteProcessBuilder {

    @Test
    public void testSimpleWithLocalhost() throws MalformedURLException, IOException, URISyntaxException {
        Process process = new RemoteProcessBuilder(new URI("localhost")).command("echo AHOJ").spawn();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        assertEquals(reader.readLine().trim(), "AHOJ");
    }

    @Test
    public void testSimpleWithLocalIP() throws MalformedURLException, IOException, URISyntaxException {
        Process process = new RemoteProcessBuilder(new URI("127.0.0.1")).command("echo AHOJ").spawn();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        assertEquals(reader.readLine().trim(), "AHOJ");
    }

}
