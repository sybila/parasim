package org.sybila.parasim.model.cdi;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestXMLServiceFactory {

    @Test
    public void testFromFile() throws IOException, URISyntaxException {
        ServiceFactory factory = XMLServiceFactory.fromFile(new File(getClass().getClassLoader().getResource("org/sybila/parasim/model/cdi/parasim-config.xml").toURI()));
        assertTrue(factory.getService(java.util.List.class) instanceof ArrayList);
    }
    
    @Test
    public void testFromFileAndDependencies() throws IOException, URISyntaxException {
        ServiceFactory factory = XMLServiceFactory.fromFile(new File(getClass().getClassLoader().getResource("org/sybila/parasim/model/cdi/parasim-config.xml").toURI()));
        assertTrue(factory.getService(java.lang.String.class) instanceof String);
        assertEquals(factory.getService(java.lang.String.class), "INJECTED");
    }
    
}
