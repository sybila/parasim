package org.sybila.parasim.core.extension.cdi.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sybila.parasim.core.ProviderImpl;
import org.sybila.parasim.core.ProvidingFieldPoint;
import org.sybila.parasim.core.ProvidingMethodPoint;
import org.sybila.parasim.core.ProvidingPoint;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.annotations.Provide;
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

    @Override
    public void provideFieldsAndMethods(Object target, Context context) {
        if (target == null) {
            throw new IllegalArgumentException("The parameter [target] is null.");
        }
        if (context == null) {
            throw new IllegalArgumentException("The parameter [context] is null.");
        }
        for (Field field: target.getClass().getDeclaredFields()) {
            if (field.getAnnotation(Provide.class) != null) {
                ProvidingPoint providingPoint = new ProvidingFieldPoint(target, field);
                Class<?> type = getType(providingPoint.getType());
                bind(type, context, providingPoint.value());
            }
        }
        for (Method method: target.getClass().getDeclaredMethods()) {
            if (method.getAnnotation(Provide.class) != null) {
                ProvidingPoint providingPoint = new ProvidingMethodPoint(target, method, context, method.getAnnotation(Provide.class).fresh());
                Class<?> type = getType(providingPoint.getType());
                bind(type, context, ProviderImpl.of(providingPoint, type).get());
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
