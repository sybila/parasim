package org.sybila.parasim.core;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class ObserverMethodImpl implements ObserverMethod{

    private Method method;
    private Object target;
    
    public ObserverMethodImpl(Object target, Method method) {
        if (target == null) {
            throw new IllegalArgumentException("The parameter [target] is null.");
        }
        if (method == null) {
            throw new IllegalArgumentException("The parameter [method] is null.");
        }
        this.target = target;
        this.method = method;
    }
    
    public void invoke(Manager manager, Object event) {
        // resolve parameters
        Object[] parameters = new Object[method.getParameterTypes().length];
        parameters[0] = event;
        for(int i=1; i<parameters.length; i++) {
            parameters[i] = manager.resolve(method.getParameterTypes()[i]);
            if (parameters[i] == null) {
                throw new InvocationException("There is no available instance for class <" + method.getParameterTypes()[i].getName() + ">.");
            }
        }
        try {
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            method.invoke(target, parameters);
        } catch(Exception e) {
            System.err.println(target.getClass().getName() + "#" + method.getName());
            throw new InvocationException(e);
        }
    }

    public Method getMethod() {
        return method;
    }

    public Type getType() {
        return method.getGenericParameterTypes()[0];
    }
    
}