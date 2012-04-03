package org.sybila.parasim.model.verification.result;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.xml.XMLException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class TestVerificationResultStorage {

    private ArrayVerificationResult result;

    @BeforeMethod
    public void initResult() {
        result = new ArrayVerificationResult(4,
                new Point[]{new ArrayPoint(0f, -0.15f, 12.3f, 53.1f),
                    new ArrayPoint(0f, 89.005f, -74.102f, 376.4f),
                    new ArrayPoint(0f, 538.01f, -35.002f, 55.12f),
                    new ArrayPoint(0f, 124.1f, 25.35f, -0.005f)},
                new float[]{0.125f, -0.145f, -54f, 12.8f});
    }

    private File getTestResultFile() {
        URL res = getClass().getClassLoader().getResource("testResult.xml");
        try {
            return new File(res.toURI());
        } catch (URISyntaxException urise) {
            urise.printStackTrace();
            fail("Could not get to test result file.");
        }
        return null;
    }

    /**
     * Tests whether resource is able to load from a file correctly.
     */
    @Test
    public void tryLoad() {
        VerificationResultResource src = new VerificationResultResource(getTestResultFile());
        try {
            src.load();
        } catch (XMLException xmle) {
            if (xmle.getCause() != null) {
                xmle.getCause().printStackTrace();
            }
            fail("XML error: " + xmle.getMessage());
        }
        assertEquals(src.getRoot(), result, "Space should be loaded correctly.");
    }

    @Test
    public void tryStore() {
        File temp = null;
        try {
            temp = File.createTempFile("result", ".xml");
        } catch (IOException ioe) {
            ioe.printStackTrace();
            fail("Could not create temporary file.");
        }
        //temp.deleteOnExit();

        VerificationResultResource src = new VerificationResultResource(temp);
        src.setRoot(result);
        try {
            src.store();
        } catch (XMLException xmle) {
            if (xmle.getCause() != null) {
                xmle.getCause().printStackTrace();
            }
            fail("XML error while storing: " + xmle.getMessage());
        }

        src.setRoot(null);

        try {
            src.load();
        } catch (XMLException xmle) {
            if (xmle.getCause() != null) {
                xmle.getCause().printStackTrace();
            }
            fail("XML error while loading: " + xmle.getMessage());
        }

        assertEquals(src.getRoot(), result, "After being stored and loaded, the result should not change.");
    }
}
