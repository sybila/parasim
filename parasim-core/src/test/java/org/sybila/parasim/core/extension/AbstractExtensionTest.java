package org.sybila.parasim.core.extension;

import java.util.ArrayList;
import java.util.Collection;
import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.ManagerImpl;
import org.sybila.parasim.core.context.ApplicationContext;
import org.sybila.parasim.core.context.Context;
import org.sybila.parasim.core.extension.loader.ExtensionLoaderExtension;
import org.testng.annotations.BeforeMethod;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractExtensionTest {
    
    private Manager manager;
    
    @BeforeMethod(dependsOnGroups={"beforeManager"})
    public void prepareManager() {
        System.setProperty("parasim.config.file", "src/test/resources/org/sybila/parasim/core/extension/configuration/parasim.xml");
        Collection<Class<? extends Context>> contexts = new ArrayList<Class<? extends Context>>();
        contexts.add(ApplicationContext.class);
        Collection<Class<?>> extensions = new ArrayList<Class<?>>();
        extensions.add(ExtensionLoaderExtension.class);
        manager = new ManagerImpl(contexts, extensions);        
    }
    
    protected Manager getManager() {
        return manager;
    }
    
}
