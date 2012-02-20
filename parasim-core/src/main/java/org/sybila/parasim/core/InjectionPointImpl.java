package org.sybila.parasim.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.sybila.parasim.core.annotations.ApplicationScope;
import org.sybila.parasim.core.annotations.Scope;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class InjectionPointImpl implements InjectionPoint {

    private Field field;
    private Class<? extends Annotation> scope;
    private Object target;
    private Type type;

    public InjectionPointImpl(Object target, Field field) {
        if (target == null) {
            throw new IllegalArgumentException("The parameter [target] is null.");
        }
        if (field == null) {
            throw new IllegalArgumentException("The parameter [field] is null.");
        }
        this.target = target;
        this.field = field;
        this.scope = loadScope(field);
        this.type = loadType(field);
    }

    public void set(Instance<?> value) {
        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            field.set(target, value);
        } catch (Exception e) {
            throw new InvocationException(e);
        }
    }

    public Class<? extends Annotation> getScope() {
        return scope;
    }
    
    public Type getType() {
        return type;
    }

    private Class<? extends Annotation> loadScope(Field field) {
        for (Annotation annotation : field.getAnnotations()) {
            for (Annotation a : annotation.annotationType().getAnnotations()) {
                if (a.getClass() == Scope.class) {
                    return annotation.getClass();
                }
            }
        }
        return ApplicationScope.class;
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