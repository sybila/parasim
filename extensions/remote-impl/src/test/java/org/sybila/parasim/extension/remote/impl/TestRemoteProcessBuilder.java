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
