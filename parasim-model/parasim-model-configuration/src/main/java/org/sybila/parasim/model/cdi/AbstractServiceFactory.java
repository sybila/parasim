package org.sybila.parasim.model.cdi;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import org.sybila.parasim.model.cdi.annotations.Inject;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractServiceFactory implements ServiceFactory {

    @Override
    public Object getService(Class<?> interfaze) {
        return getService(interfaze, false);
    }

    @Override
    public void injectFields(Object o) {
        Collection<Field> toInject = new ArrayList<Field>();
        Collection<Field> toInjectFresh = new ArrayList<Field>();
        for (Field field : o.getClass().getDeclaredFields()) {
            if (field.getAnnotation(Inject.class) != null) {
                if (field.getAnnotation(Inject.class).fresh()) {
                    toInjectFresh.add(field);
                } else {
                    toInject.add(field);
                }
            }
        }
        for (Field field : toInject) {
            injectField(o, field);
        }
        for (Field field : toInjectFresh) {
            injectField(o, field);
        }
    }

    @Override
    public void executeVoidMethod(Object o, Method method) {
        Object[] paramValues = new Object[method.getParameterTypes().length];
        for (int i = 0; i < method.getTypeParameters().length; i++) {
            if (!isServiceAvailable(method.getParameterTypes()[i])) {
                throw new IllegalStateException("The service " + method.getParameterTypes()[i].getCanonicalName() + " requested in " + o.getClass().getCanonicalName() + " is not available.");
            }
            paramValues[i] = getService(method.getParameterTypes()[i]);
        }
        try {
            method.setAccessible(true);
            method.invoke(o, paramValues);
        } catch (Exception e) {
            throw new IllegalStateException("The method can't be executed.", e);
        }
    }

    protected Object createInstance(Class<?> clazz, Object... parameters) {
        findConstructor : for (Constructor construtor : clazz.getConstructors()) {
            if (construtor.getParameterTypes().length != parameters.length) {
                continue findConstructor;
            }
            for (int i=0; i<parameters.length; i++) {
                if (!construtor.getParameterTypes()[i].isInstance(parameters[i])) {
                    if (construtor.getParameterTypes()[i].equals(int.class) && parameters[i] instanceof Integer) {
                        continue;
                    }
                    if (construtor.getParameterTypes()[i].equals(float.class) && parameters[i] instanceof Float) {
                        continue;
                    }
                    if (construtor.getParameterTypes()[i].equals(boolean.class) && parameters[i] instanceof Boolean) {
                        continue;
                    }
                    if (construtor.getParameterTypes()[i].equals(double.class) && parameters[i] instanceof Double) {
                        continue;
                    }
                    if (construtor.getParameterTypes()[i].equals(long.class) && parameters[i] instanceof Long) {
                        continue;
                    }
                    if (construtor.getParameterTypes()[i].equals(char.class) && parameters[i] instanceof Character) {
                        continue;
                    }
                    if (construtor.getParameterTypes()[i].equals(byte.class) && parameters[i] instanceof Byte) {
                        continue;
                    }
                    continue findConstructor;
                }
            }
            try {
                return construtor.newInstance(parameters);
            } catch (Exception e) {
                throw new IllegalStateException("The class " + clazz.getCanonicalName() + " can't be instantiated.", e);
            }
        }
        throw new IllegalStateException("The class " + clazz.getCanonicalName() + " hasn't any constructor with the given parameters");        
    }    
    
    private void injectField(Object object, Field field) {
        Object[] params = new Object[field.getAnnotation(Inject.class).parameters().length];
        for (int i = 0; i < field.getAnnotation(Inject.class).parameters().length; i++) {
            try {
                Field paramField = object.getClass().getDeclaredField(field.getAnnotation(Inject.class).parameters()[i]);
                paramField.setAccessible(true);
                params[i] = paramField.get(object);
            } catch (Exception e) {
                throw new IllegalStateException("The parameter field [" + field.getAnnotation(Inject.class).parameters()[i] + "] can't be taken.", e);
            }
        }
        Object service = getService(field.getType(), field.getAnnotation(Inject.class).fresh(), params);
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
