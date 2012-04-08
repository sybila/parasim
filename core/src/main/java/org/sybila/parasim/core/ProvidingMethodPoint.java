package org.sybila.parasim.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.sybila.parasim.core.context.Context;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ProvidingMethodPoint extends AbstractTypedMethod implements ProvidingPoint {

    private final boolean fresh;
    private final Context context;
    
    public ProvidingMethodPoint(Object target, Method method, Context context, boolean fresh) {
        super(target, method);
        if (context == null) {
            throw new IllegalArgumentException("The paramater [context] is null.");
        }
        this.fresh = fresh;
        this.context = context;
    }
    
    public boolean fresh() {
        return fresh;
    }

    public Object value() {
        Object[] params = new Object[getMethod().getParameterTypes().length];
        for (int i=0; i<params.length; i++) {
            params[i] = context.resolve(getMethod().getParameterTypes()[i]);
            if (params[i] == null) {
                throw new InvocationException("There is no available instance for class <" + getMethod().getParameterTypes()[i].getName() + ">.");
            }
        }
        try {
            if (!getMethod().isAccessible()) {
                getMethod().setAccessible(true);
            }
            return getMethod().invoke(getTarget(), params);
        } catch (IllegalAccessException ex) {
            throw new InvocationException(ex);
        } catch (IllegalArgumentException ex) {
            throw new InvocationException(ex);
        } catch (InvocationTargetException ex) {
            throw new InvocationException(ex);
        }
    }
}
