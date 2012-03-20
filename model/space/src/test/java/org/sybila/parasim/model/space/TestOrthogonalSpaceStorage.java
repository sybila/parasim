package org.sybila.parasim.model.space;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.xml.XMLException;
import org.testng.annotations.Test;
import static org.testng.Assert.fail;
import static org.testng.Assert.assertEquals;


/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class TestOrthogonalSpaceStorage {
    
    private static OrthogonalSpace getTestSpace() {
        Point min = new ArrayPoint(0, -0.149f, 129.1f, -591.023f, -8.137f);
        Point max = new ArrayPoint(0, 5.931f, 251.9f, -0.006f, 2.041f);
        return new OrthogonalSpace(min, max);
    }
    
    private File getTestSpaceFile() {
        URL res = getClass().getClassLoader().getResource("testSpace.xml");
        try {
            return new File(res.toURI());
        } catch (URISyntaxException urise) {
            urise.printStackTrace();
            fail("Could not get to test formula file.");
        }
        return null;
    } 

    /**
     * Tests whether resource is able to load a space from a file correctly.
     */
    @Test
    public void tryLoad() {
        OrthogonalSpaceResource resource = new OrthogonalSpaceResource(getTestSpaceFile());
        try {
            resource.load();
        } catch (XMLException xmle) {
            if (xmle.getCause() != null) {
                xmle.getCause().printStackTrace();
            }
            fail("XML error: " + xmle.getMessage());
        }
        assertEquals(resource.getRoot(), getTestSpace(), "Space should be loaded correctly.");
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
        OrthogonalSpaceResource resource = new OrthogonalSpaceResource(temp);
        resource.setRoot(getTestSpace());
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
        
        assertEquals(resource.getRoot(), getTestSpace(), "After being stored and loaded, the space should not change.");
        
    }
    
}