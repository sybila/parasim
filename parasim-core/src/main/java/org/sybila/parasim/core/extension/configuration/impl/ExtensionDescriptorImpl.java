package org.sybila.parasim.core.extension.configuration.impl;

import org.sybila.parasim.core.extension.configuration.api.Property;
import org.sybila.parasim.core.extension.configuration.api.ExtensionDescriptor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ExtensionDescriptorImpl implements ExtensionDescriptor {
   
    private String name;
    private Map<String, Property> properties = new HashMap<String, Property>();
    
    public ExtensionDescriptorImpl(String name) {
        if (name == null) {
            throw new IllegalArgumentException("The parameter [name] is null.");
        }
        this.name = name;
    }

    public boolean containsProperty(String name) {
        return properties.containsKey(name);
    }
    
    public String getName() {
        return name;
    }

    public String getProperty(String name) {
        return getProperty(name, null);
    }

    public String getProperty(String name, String defaultValue) {
        Property prop = properties.get(name);
        return prop == null ? defaultValue : prop.getValue();
    }
    
    public ExtensionDescriptor setProperty(String name, String value) {
        properties.put(name, new Property(name, value));
        return this;
    }

    public Iterator<Property> iterator() {
        return properties.values().iterator();
    }
    
}
