package org.sybila.parasim.core.extension.cdi.impl;

import java.lang.reflect.Field;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.context.Context;
import org.sybila.parasim.core.extension.cdi.api.ServiceFactory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractServiceFactory implements ServiceFactory {

    @Override
    public void injectFields(Object target, Context context) {
        if (target == null) {
            throw new IllegalArgumentException("The parameter [target] is null.");
        }
        if (context == null) {
            throw new IllegalArgumentException("The parameter [context] is null.");
        }
        for (Field field : target.getClass().getDeclaredFields()) {
            if (field.getAnnotation(Inject.class) != null) {
               injectField(target, field, context);
            }
        }
    }

    private void injectField(Object object, Field field, Context context) {
        Object service = getService(field.getType(), context);
        if (service == null) {
            throw new IllegalStateException("The service " + field.getType().getName() + " requested in " + object.getClass().getName() + " is not available.");
        }
        field.setAccessible(true);
        try {
            field.set(object, field.getType().cast(service));
        } catch (Exception ex) {
            throw new IllegalStateException("The service can't be injected.", ex);
        }
    }
}
