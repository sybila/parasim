package org.sybila.parasim.core.extension.configuration.api;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface ExtensionDescriptor extends Iterable<Property> {

    boolean containsProperty(String name);

    String getName();

    String getProperty(String name);

    String getProperty(String name, String defaultValue);

    String[] getPropertyAsArray(String name);

    String[] getPropertyAsArray(String name, String[] defaultValue);

    boolean isPropertyArray(String name);

    ExtensionDescriptor setProperty(String name, String value);

    ExtensionDescriptor setProperty(String name, String[] value);
}
