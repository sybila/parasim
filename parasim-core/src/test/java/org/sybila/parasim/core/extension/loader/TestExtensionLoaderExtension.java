package org.sybila.parasim.core.extension.loader;

import java.util.ArrayList;
import java.util.Collection;
import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.ManagerImpl;
import org.sybila.parasim.core.context.ApplicationContext;
import org.sybila.parasim.core.context.Context;
import org.sybila.parasim.core.extension.AbstractExtensionTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestExtensionLoaderExtension extends AbstractExtensionTest {
    
    public static long managerStarted;
    public static long extensionRegistered;
    
    @BeforeMethod(groups="beforeManager")
    public void prepare() {
        managerStarted = 0;
        extensionRegistered = 0;
    }
    
    @Test
    public void testLoad() {
        getManager().start();
        getManager().shutdown();
        assertNotEquals((long) 0, extensionRegistered);
        assertNotEquals((long) 0, managerStarted);
        assertTrue(extensionRegistered < managerStarted);
    }
    
}

