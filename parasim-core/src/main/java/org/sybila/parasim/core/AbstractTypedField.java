package org.sybila.parasim.core;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractTypedField implements Typed {
    
    private Field field;
    private Object target;
    private Type type;    
    
    public AbstractTypedField(Object target, Field field) {
        if (target == null) {
            throw new IllegalArgumentException("The parameter [target] is null.");
        }
        if (field == null) {
            throw new IllegalArgumentException("The parameter [field] is null.");
        }
        this.target = target;
        this.field = field;
        this.type = loadType(field);        
    }
    
    public Type getType() {
        return type;
    }
    
    protected Field getField() {
        return field;
    }
    
    protected Object getTarget() {
        return target;
    }
    
    private Type loadType(Field field) {
        ParameterizedType loadedType = (ParameterizedType) field.getGenericType();
        if(loadedType.getActualTypeArguments()[0] instanceof ParameterizedType) {
            ParameterizedType first = (ParameterizedType)loadedType.getActualTypeArguments()[0];
            return (Class<?>)first.getRawType();
        } else {
            return (Class<?>)loadedType.getActualTypeArguments()[0];
        }
    }    
    
}
