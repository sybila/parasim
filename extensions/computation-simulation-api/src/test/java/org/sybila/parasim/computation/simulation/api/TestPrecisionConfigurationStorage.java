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
package org.sybila.parasim.computation.simulation.api;

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
public class TestPrecisionConfigurationStorage {
    
    private PrecisionConfiguration precisionConfiguration;
    
    @BeforeMethod
    public void preparePrecisionConfiguration() {
        precisionConfiguration = new ArrayPrecisionConfiguration(
            new float[] {0.01f, 0.02f, 0.03f},
            0.1f
        );
    }

    /**
     * Tests whether resource is able to load a space from a file correctly.
     */
    @Test
    public void tryLoad() {
        PrecisionConfigurationResource resource = new PrecisionConfigurationResource(getTestFile());
        try {
            resource.load();
        } catch (XMLException xmle) {
            if (xmle.getCause() != null) {
                xmle.getCause().printStackTrace();
            }
            fail("XML error: " + xmle.getMessage());
        }
        assertEquals(resource.getRoot().getDimension(), precisionConfiguration.getDimension(), "Space should be loaded correctly. Dimension doesn't match.");
        assertEquals(resource.getRoot().getMaxRelativeError(), precisionConfiguration.getMaxRelativeError(), "Space should be loaded correctly. Relative error doesn't match.");
        for (int dim=0; dim<precisionConfiguration.getDimension(); dim++) {
           assertEquals(resource.getRoot().getMaxAbsoluteError(dim), precisionConfiguration.getMaxAbsoluteError(dim), "Space should be loaded correctly. Absolute error in dimension <"+dim+">doesn't match.");
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
        PrecisionConfigurationResource resource = new PrecisionConfigurationResource(temp);
        resource.setRoot(precisionConfiguration);
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
        
        assertEquals(resource.getRoot().getDimension(), precisionConfiguration.getDimension(), "Space should be loaded correctly. Dimension doesn't match.");
        assertEquals(resource.getRoot().getMaxRelativeError(), precisionConfiguration.getMaxRelativeError(), "Space should be loaded correctly. Relative error doesn't match.");
        for (int dim=0; dim<precisionConfiguration.getDimension(); dim++) {
           assertEquals(resource.getRoot().getMaxAbsoluteError(dim), precisionConfiguration.getMaxAbsoluteError(dim), "Space should be loaded correctly. Absolute error in dimension <"+dim+">doesn't match.");
        }
        
    }    
    
    private File getTestFile() {
        URL res = getClass().getClassLoader().getResource("testPrecisionConfiguration.xml");
        try {
            return new File(res.toURI());
        } catch (URISyntaxException urise) {
            urise.printStackTrace();
            fail("Could not get to test formula file.");
        }
        return null;        
    }
    
}
