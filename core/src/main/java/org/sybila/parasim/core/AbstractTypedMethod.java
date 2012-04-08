package org.sybila.parasim.core;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class AbstractTypedMethod extends AbstractTyped {

    private final Method method;
    private final Type type;    
    
    public AbstractTypedMethod(Object target, Method method) {
        super(target);
        if (method == null) {
            throw new IllegalArgumentException("The parameter [method] is null.");
        }
        this.method = method;
        this.type = loadType(method.getGenericReturnType());
    }
    
    public Type getType() {
        return type;
    }

    protected Method getMethod() {
        return method;
    }
}
