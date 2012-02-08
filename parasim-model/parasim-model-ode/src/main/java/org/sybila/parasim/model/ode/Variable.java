package org.sybila.parasim.model.ode;

/**
 * @author <a href="mailto:jpapouse@fi.muni.cz">Jan Papousek</a>
 */
public class Variable {
    
    private int index;
    
    private String name;
    
    public Variable(String name, int index) {
        if (name == null) {
            throw new IllegalArgumentException("The parameter [name] is null.");
        }
        if (index < 0) {
            throw new IllegalArgumentException("The parameter [index] has to be a positive number.");
        }
        this.name = name;
        this.index = index;
    }
    
    public int getIndex() {
        return index;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
}
