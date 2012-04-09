package org.sybila.parasim.core;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class AbstractTyped implements Typed {

    private final Object target;

    public AbstractTyped(Object target) {
        if (target == null) {
            throw new IllegalArgumentException("The parameter [target] is null.");
        }
        this.target = target;
    }

    protected Object getTarget() {
        return target;
    }

    protected Type loadType(Type type) {
        if (!(type instanceof ParameterizedType)) {
            return type;
        }
        ParameterizedType loadedType = (ParameterizedType) type;
        if(loadedType.getActualTypeArguments()[0] instanceof ParameterizedType) {
            ParameterizedType first = (ParameterizedType)loadedType.getActualTypeArguments()[0];
            return (Class<?>)first.getRawType();
        } else {
            return (Class<?>)loadedType.getActualTypeArguments()[0];
        }
    }

}
