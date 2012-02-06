package org.sybila.parasim.model.cdi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.sybila.parasim.model.cdi.annotations.Inject;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractCDIFactory implements CDIFactory {
    
    @Override
    public void injectFields(Object o) {
        for (Field field : o.getClass().getDeclaredFields()) {
            if (field.getAnnotation(Inject.class) != null) {
                Object service = getService(field.getType());
                if (service == null) {
                    throw new IllegalStateException("The service " + field.getType().getCanonicalName() + " requested in " + o.getClass().getCanonicalName() + " is not available.");
                }
                field.setAccessible(true);
                try {
                    field.set(o, field.getType().cast(service));
                } catch (IllegalAccessException ex) {
                    throw new IllegalStateException("The service can't be injected.", ex);
                }
            }
        }
    }
    
    @Override
    public void executeVoidMethod(Object o, Method method) {
        Object[] paramValues = new Object[method.getParameterTypes().length];
        for (int i=0; i<method.getTypeParameters().length; i++) {
            if (!isServiceAvailable(method.getParameterTypes()[i])) {
                throw new IllegalStateException("The service " + method.getParameterTypes()[i].getCanonicalName() + " requested in " + o.getClass().getCanonicalName() + " is not available.");
            }
            paramValues[i] = getService(method.getParameterTypes()[i]);
        }
        try {
            method.setAccessible(true);
            method.invoke(o, paramValues);
        } catch(Exception e) {
            throw new IllegalStateException("The method can't be executed.", e);
        }
    }
    
}
