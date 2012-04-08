package org.sybila.parasim.core;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractTypedField extends AbstractTyped {
    
    private final Field field;
    private final Type type;    
    
    public AbstractTypedField(Object target, Field field) {
        super(target);
        if (field == null) {
            throw new IllegalArgumentException("The parameter [field] is null.");
        }
        this.field = field;
        this.type = loadType(field.getGenericType());        
    }
    
    public Type getType() {
        return type;
    }
    
    protected Field getField() {
        return field;
    }  
    
}
