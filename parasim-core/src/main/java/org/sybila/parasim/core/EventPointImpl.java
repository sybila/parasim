/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sybila.parasim.core;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class EventPointImpl implements EventPoint {
    
    private Field field;
    private Object target;
    private Type type;
        
    public EventPointImpl(Object target, Field field) {
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

    public void set(Event<?> value) throws InvocationException {
        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            field.set(target, value);
        } catch(Exception e) {
            throw new InvocationException(e);
        }
    }

    public Type getType() {
        return type;
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
