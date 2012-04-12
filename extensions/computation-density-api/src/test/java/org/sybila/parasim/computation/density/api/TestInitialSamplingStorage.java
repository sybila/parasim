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
package org.sybila.parasim.computation.density.api;

import java.io.IOException;
import org.sybila.parasim.model.xml.XMLException;
import org.testng.annotations.Test;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import org.testng.annotations.BeforeMethod;
import static org.testng.Assert.fail;
import static org.testng.Assert.assertEquals;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestInitialSamplingStorage {
    
    private InitialSampling initialSampling;
    
    @BeforeMethod
    public void preparePrecisionConfiguration() {
        initialSampling = new ArrayInitialSampling(1, 2, 3, 4);
    }

    /**
     * Tests whether resource is able to load a space from a file correctly.
     */
    @Test
    public void tryLoad() {
        InitialSamplingResource resource = new InitialSamplingResource(getTestFile());
        try {
            resource.load();
        } catch (XMLException xmle) {
            if (xmle.getCause() != null) {
                xmle.getCause().printStackTrace();
            }
            fail("XML error: " + xmle.getMessage());
        }
        assertEquals(resource.getRoot().getDimension(), initialSampling.getDimension(), "Sampling should be loaded correctly. Dimension doesn't match.");
        for (int dim=0; dim<initialSampling.getDimension(); dim++) {
           assertEquals(resource.getRoot().getNumberOfSamples(dim), initialSampling.getNumberOfSamples(dim), "Sampling should be loaded correctly. Number of samples in dimension <"+dim+"> doesn't match.");
        }
    }    
    
    /**
     * Tests whether resource is able to store and then load a space correctly.
     */
    @Test
    public void tryStoreLoad() {
        //create temporar file
        File temp = null;
        try {
            temp = File.createTempFile("space", ".xml");
        } catch (IOException ioe) {
            ioe.printStackTrace();
            fail("Temporary file could not be created.");
        }
        temp.deleteOnExit();
        
        //store
        InitialSamplingResource resource = new InitialSamplingResource(temp);
        resource.setRoot(initialSampling);
        try {
            resource.store();
        } catch (XMLException xmle) {
            if (xmle.getCause() != null) {
                xmle.getCause().printStackTrace();
            }
            fail("XML error while storing: " + xmle.getMessage());
        }
        
        //reset
        resource.setRoot(null);
        
        //load
        try {
            resource.load();
        } catch (XMLException xmle) {
            if (xmle.getCause() != null) {
                xmle.getCause().printStackTrace();
            }
            fail("XML error while loading: " + xmle.getMessage());
        }

        assertEquals(resource.getRoot().getDimension(), initialSampling.getDimension(), "Sampling should be loaded correctly. Dimension doesn't match.");
        for (int dim=0; dim<initialSampling.getDimension(); dim++) {
           assertEquals(resource.getRoot().getNumberOfSamples(dim), initialSampling.getNumberOfSamples(dim), "Sampling should be loaded correctly. Number of samples in dimension <"+dim+"> doesn't match.");
        }
    }    
    
    private File getTestFile() {
        URL res = getClass().getClassLoader().getResource("testInitialSampling.xml");
        try {
            return new File(res.toURI());
        } catch (URISyntaxException urise) {
            urise.printStackTrace();
            fail("Could not get to test formula file.");
        }
        return null;        
    }
    
}
