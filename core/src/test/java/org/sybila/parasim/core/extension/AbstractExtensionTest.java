package org.sybila.parasim.core.extension;

import org.sybila.parasim.core.ManagerImpl;
import org.testng.annotations.BeforeMethod;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractExtensionTest {
    
    private ManagerImpl manager;
    
    @BeforeMethod(dependsOnGroups={"beforeManager"})
    public void prepareManager() throws Exception {
        System.setProperty("parasim.config.file", "src/test/resources/org/sybila/parasim/core/extension/configuration/parasim.xml");
        manager = (ManagerImpl) ManagerImpl.create();
    }
    
    protected ManagerImpl getManager() {
        return manager;
    }
    
}
