package org.sybila.parasim.core.extension.logging;

import java.net.URL;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class LoggingConfiguration {

    private URL configFile = LoggingConfiguration.class.getClassLoader().getResource("org/sybila/parasim/log4j/log4j.properties");
    private String level;

    public URL getConfigFile() {
        return configFile;
    }

    public String getLevel() {
        return level;
    }
}
