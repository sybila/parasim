package org.sybila.parasim.core.extension.configuration.impl;

import java.io.IOException;
import org.sybila.parasim.core.Event;
import org.sybila.parasim.core.Instance;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.annotations.Observes;
import org.sybila.parasim.core.event.ManagerStarted;
import org.sybila.parasim.core.extension.configuration.api.ExtensionDescriptorMapper;
import org.sybila.parasim.core.extension.configuration.api.ParasimDescriptor;
import org.sybila.parasim.core.extension.configuration.api.event.ConfigurationLoaded;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class DescriptorLoaderRegistrar {
    
    @Inject
    private Instance<ParasimDescriptor> parasimDescriptor;
    @Inject
    private Instance<ExtensionDescriptorMapper> extensionDescriptorMapper;
    @Inject
    private Event<ConfigurationLoaded> event;
    
    public void loadDescriptor(@Observes ManagerStarted event) throws IOException, SAXException {
        parasimDescriptor.set(ParasimDescriptorImpl.fromXMLFile("parasim.config.file", "parasim.xml"));
        extensionDescriptorMapper.set(new ExtensionDescriptorMapperImpl());
        this.event.fire(new ConfigurationLoaded());
    }
    
}
