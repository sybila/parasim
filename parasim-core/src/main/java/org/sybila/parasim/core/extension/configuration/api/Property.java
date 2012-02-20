package org.sybila.parasim.core.extension.configuration.api;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class Property {
   
    private String name;
    private String value;
    
    public Property(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("The parameter [name] is null.");
        }
        if (value == null) {
            throw new IllegalArgumentException("The parameter [value] is null.");
        }
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
    
}
