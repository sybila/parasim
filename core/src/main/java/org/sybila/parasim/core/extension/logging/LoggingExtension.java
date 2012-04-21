package org.sybila.parasim.core.extension.logging;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.core.annotations.Observes;
import org.sybila.parasim.core.extension.configuration.api.ExtensionDescriptor;
import org.sybila.parasim.core.extension.configuration.api.ExtensionDescriptorMapper;
import org.sybila.parasim.core.extension.configuration.api.ParasimDescriptor;
import org.sybila.parasim.core.extension.configuration.api.event.ConfigurationLoaded;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class LoggingExtension {

    public void configureLogging(@Observes ConfigurationLoaded event, ParasimDescriptor descriptor, ExtensionDescriptorMapper mapper) throws IllegalAccessException, FileNotFoundException, IOException {
        LoggingConfiguration configuration = new LoggingConfiguration();
        ExtensionDescriptor extensionDescriptor = descriptor.getExtensionDescriptor("logging");
        if (extensionDescriptor != null) {
            mapper.map(extensionDescriptor, configuration);
        }
        Properties prop = new Properties();
        prop.load(configuration.getConfigFile().openStream());
        PropertyConfigurator.configure(configuration.getConfigFile());
        if (configuration.getLevel() != null && !configuration.getLevel().isEmpty()) {
            prop.setProperty("log4j.rootLogger", configuration.getLevel() + ", stdout");
        }
        PropertyConfigurator.configure(prop);
        LoggerFactory.getLogger(getClass()).debug("logging configured");
    }

}
