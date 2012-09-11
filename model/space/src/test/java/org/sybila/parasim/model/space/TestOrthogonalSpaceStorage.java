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
public class TestOrthogonalSpaceStorage extends AbstractOrthogonalSpaceTest {

    private static OrthogonalSpaceImpl getTestSpace() {
        Point min = new ArrayPoint(1f, -0.149f, 129.1f, -591.023f, -8.137f);
        Point max = new ArrayPoint(10.11f, 5.931f, 251.9f, -0.006f, 2.041f);
        return new OrthogonalSpaceImpl(min, max, createOdeSystem());
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
        OrthogonalSpaceResource resource = new OrthogonalSpaceResource(getTestSpaceFile(), createOdeSystem());
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
        OrthogonalSpaceResource resource = new OrthogonalSpaceResource(temp, createOdeSystem());
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