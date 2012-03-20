package org.sybila.parasim.core.extension.cdi.impl;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.context.Context;
import org.sybila.parasim.core.extension.cdi.api.ServiceFactory;
import org.sybila.parasim.core.extension.cdi.api.annotations.Provide;

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

    @Override
    public void provideFields(Object target, Context context) {
        if (target == null) {
            throw new IllegalArgumentException("The parameter [target] is null.");
        }
        if (context == null) {
            throw new IllegalArgumentException("The parameter [context] is null.");
        }
        for (Field field: target.getClass().getDeclaredFields()) {
            if (field.getAnnotation(Provide.class) != null) {
                if (field.getType().isPrimitive()) {
                    throw new IllegalStateException("The class [" + target.getClass().getName() + "] can't provide value of primitive type field [" + field.getName() + "]");
                }
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                try {
                    if (field.get(target) != null) {
                        bind(getType(field.getType()), context, field.get(target));
                    } else if(field.getAnnotation(Provide.class).strict()) {
                        throw new IllegalStateException("The field [" + field.getName() + "] in the class [" + target.getClass().getName() + "] providing values isn't initialized.");
                    }
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(AbstractServiceFactory.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    abstract protected <T> void bind(Class<T> clazz, Context context, Object value);

    private static Class<?> getType(Type type) {
        // type is not parametrized
        if (type instanceof  Class<?>) {
            return (Class<?>) type;
        }
        // type is parametrized
        if (type instanceof ParameterizedType) {
            return getType(((ParameterizedType) type).getActualTypeArguments()[0]);
        }
        // type is wildcard;
        if (type instanceof WildcardType) {
            for (Type wildType: ((WildcardType) type).getUpperBounds()) {
                Class<?> upperBoundsType = getType(wildType);
                if (upperBoundsType != null) {
                    return upperBoundsType;
                }
            }
            for (Type wildType: ((WildcardType) type).getLowerBounds()) {
                Class<?> lowerBoundsType = getType(wildType);
                if (lowerBoundsType != null) {
                    return lowerBoundsType;
                }
            }
        }
        // not success
        return null;
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
