/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sybila.parasim.core.extension.loader;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.sybila.parasim.core.Instance;
import org.sybila.parasim.core.LoadableExtension;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.annotations.Observes;
import org.sybila.parasim.core.event.ManagerStarted;
import org.sybila.parasim.core.extension.cdi.TestServiceFactoryExtension;
import org.sybila.parasim.core.extension.cdi.api.ServiceFactory;
import org.sybila.parasim.core.extension.configuration.api.ExtensionDescriptorMapper;
import org.sybila.parasim.core.extension.configuration.impl.TestExtensionDescriptorMapperImpl;
import org.sybila.parasim.core.extension.loader.api.ExtensionBuilder;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestedLoadableExtension implements LoadableExtension {
    
    @Inject
    private Instance<String> toInject;
    
    public void observesManagerStarted(@Observes ManagerStarted event) {
        TestExtensionLoaderExtension.managerStarted = System.currentTimeMillis();
        toInject.set("HELLO");
    }
    
    public void observesServiceFactory(@Observes ServiceFactory serviceFactory) {
        TestServiceFactoryExtension.serviceFactory = serviceFactory;
    }
    
    public void observesExtensionDescriptorMapper(@Observes ExtensionDescriptorMapper mapper) {
        TestExtensionDescriptorMapperImpl.mapper = mapper;
    }
    
    public void register(ExtensionBuilder builder) {
        try {
            builder.extension(TestedLoadableExtension.class);
            TestExtensionLoaderExtension.extensionRegistered = System.currentTimeMillis();
            Thread.sleep(50);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestedLoadableExtension.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}