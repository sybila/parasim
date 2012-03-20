package org.sybila.parasim.core.extension.configuration.api;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class Property<V> {
   
    private String name;
    private V value;
    
    private Property(String name, V value) {
        if (name == null) {
            throw new IllegalArgumentException("The parameter [name] is null.");
        }
        if (value == null) {
            throw new IllegalArgumentException("The parameter [value] is null.");
        }
        this.name = name;
        this.value = value;
    }

    public static <T> Property<T> of(String name, T value) {
        return new Property<T>(name, value);
    }
    
    public String getName() {
        return name;
    }

    public V getValue() {
        return value;
    }
    
}
