package org.sybila.parasim.computation.simulation;

import org.sybila.parasim.core.context.Context;
import java.lang.annotation.Annotation;
import org.sybila.parasim.computation.lifecycle.api.annotations.ComputationScope;
import org.sybila.parasim.computation.simulation.api.AdaptiveStepConfiguration;
import org.sybila.parasim.computation.simulation.api.AdaptiveStepSimulator;
import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.ManagerImpl;
import org.sybila.parasim.core.context.AbstractContext;
import org.sybila.parasim.core.extension.configuration.api.ExtensionDescriptorMapper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestLoadableExtension {
    
    private Manager manager;
    
    @BeforeMethod
    public void startManager() throws Exception {
        System.setProperty("parasim.config.file", "src/test/resources/org/sybila/parasim/computation/simulation/parasim.xml");
        manager = ManagerImpl.create();
        manager.start();
        assertNotNull(manager.resolve(ExtensionDescriptorMapper.class, manager.getRootContext()));
    }
    
    @AfterMethod
    public void stopManager() {
        manager.shutdown();
    }
    
    @Test
    public void testSimulatorLoaded() {
        assertNotNull(manager.resolve(AdaptiveStepSimulator.class, manager.getRootContext()));
    }
    
    @Test
    public void testExtensionConfigurationLoaded() {
        assertNotNull(manager.resolve(ExtensionConfiguration.class, manager.getRootContext()));
    }
    
    @Test
    public void testConfigurationLoaded() {
        Context context = new AbstractContext() {
            public Class<? extends Annotation> getScope() {
                return ComputationScope.class;
            }
        };
        manager.initializeContext(context);
        assertNotNull(manager.resolve(AdaptiveStepConfiguration.class, context));
    }
    
}
