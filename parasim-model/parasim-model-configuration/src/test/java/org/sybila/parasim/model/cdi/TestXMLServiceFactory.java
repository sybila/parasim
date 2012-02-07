package org.sybila.parasim.model.cdi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestXMLServiceFactory {

    @Test
    public void testFromFile() throws IOException {
        ServiceFactory factory = XMLServiceFactory.fromFile(new File("src/test/xml/parasim-config.xml"));
        assertTrue(factory.getService(java.util.List.class) instanceof ArrayList);
    }
    
    @Test
    public void testFromFileAndDependencies() throws IOException {
        ServiceFactory factory = XMLServiceFactory.fromFile(new File("src/test/xml/parasim-config.xml"));
        assertTrue(factory.getService(java.lang.String.class) instanceof String);
        assertEquals(factory.getService(java.lang.String.class), "INJECTED");
    }
    
}
