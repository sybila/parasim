package org.sybila.parasim.core.extension;

import java.util.ArrayList;
import java.util.Collection;
import org.sybila.parasim.core.ManagerImpl;
import org.sybila.parasim.core.extension.loader.ExtensionLoaderExtension;
import org.testng.annotations.BeforeMethod;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractExtensionTest {
    
    private ManagerImpl manager;
    
    @BeforeMethod(dependsOnGroups={"beforeManager"})
    public void prepareManager() throws Exception {
        System.setProperty("parasim.config.file", "src/test/resources/org/sybila/parasim/core/extension/configuration/parasim.xml");
        Collection<Class<?>> extensions = new ArrayList<Class<?>>();
        extensions.add(ExtensionLoaderExtension.class);
        manager = (ManagerImpl) ManagerImpl.create(extensions);
    }
    
    protected ManagerImpl getManager() {
        return manager;
    }
    
}
