package org.sybila.parasim.core.extension.configuration.impl;

import org.sybila.parasim.core.extension.configuration.api.ExtensionDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URL;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ExtensionDescriptorMapperImpl {
    
    public void map(ExtensionDescriptor descriptor, Object configBean) throws IllegalAccessException {
        if (descriptor == null) {
            throw new IllegalArgumentException("The parameter [descriptor] is null.");
        }
        if (configBean == null) {
            throw new IllegalArgumentException("The parameter [configBean] is null.");
        }
        for (Field field: configBean.getClass().getDeclaredFields()) {
            if (Modifier.isFinal(field.getModifiers())) {
                continue;
            }
            if (!descriptor.containsProperty(field.getName())) {
                continue;
            }
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            field.set(configBean, convert(getType(field), descriptor.getProperty(field.getName())));
        }
    }
    
    private <T> T convert(Class<T> type, String value) {
        if (String.class.equals(type)) {
            return type.cast(value);
        } else if (Integer.class.equals(type)) {
            return type.cast(Integer.valueOf(value));
        } else if (Byte.class.equals(type)) {
            return type.cast(Byte.valueOf(value));
        } else if (Double.class.equals(type)) {
            return type.cast(Double.valueOf(value));
        } else if (Float.class.equals(type)) {
            return type.cast(Float.valueOf(value));
        } else if (Long.class.equals(type)) {
            return type.cast(Long.valueOf(value));
        } else if (Boolean.class.equals(type)) {
            return type.cast(Boolean.valueOf(value));
        } else {
            try {
                if (URL.class.equals(type)) {
                    return type.cast(new URL(value));
                } else if(URI.class.equals(type)) {
                    return type.cast(new URI(value));
                }
            } catch(Exception e) {
                throw new IllegalArgumentException("Unable to convert value [" + value + "] to URL/URI.");
            }
            throw new IllegalArgumentException("Unable to convert value [" + value + "] to " + type.getName() + ".");
        }
    }
    
    private Class<?> getType(Field field) {
        if (!field.getType().isPrimitive()) {
            return field.getType();
        }
        Class<?> primitive = field.getType();
        if (int.class.equals(primitive)) {
            return Integer.class;
        } else if (long.class.equals(primitive)) {
            return Long.class;
        } else if (float.class.equals(primitive)) {
            return Float.class;
        } else if (double.class.equals(primitive)) {
            return Double.class;
        } else if (short.class.equals(primitive)) {
            return Short.class;
        } else if (boolean.class.equals(primitive)) {
            return Boolean.class;
        } else if (char.class.equals(primitive)) {
            return Character.class;
        } else if (byte.class.equals(primitive)) {
            return Byte.class;
        }
        throw new IllegalArgumentException();
    }
    
}
