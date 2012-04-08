package org.sybila.parasim.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.apache.commons.lang3.ArrayUtils;
import org.sybila.parasim.core.context.Context;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ProviderImpl<I> implements Provider<I> {

    private final I value;
    
    private ProviderImpl(final ProvidingPoint providingPoint, Class<I> type) {
        if (providingPoint == null) {
            throw new IllegalArgumentException("The parameter [providingPoint] is null.");
        }
        if (type == null) {
            throw new IllegalArgumentException("The parameter [type] is null.");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("The type of the provider has to be an interface, <" + type.getName() + "> given.");
        }
        if (type.isPrimitive()) {
            throw new IllegalArgumentException("The primitive type can't be provided.");
        }
        this.value = type.cast(
            Proxy.newProxyInstance(
                type.getClassLoader(),
                ArrayUtils.addAll(new Class<?>[] {type}, type.getInterfaces()),
                new InvocationHandler() {
                    
                    private Object toDelegate;
                    
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if (providingPoint.fresh()) {
                            if (!method.isAccessible()) {
                                method.setAccessible(true);
                            }
                            return method.invoke(providingPoint.value(), args);
                        } else {
                            if (toDelegate == null) {
                                toDelegate = providingPoint.value();
                            }
                            if (!method.isAccessible()) {
                                method.setAccessible(true);
                            }
                            return method.invoke(toDelegate, args);
                        }
                    }
                }
            )
        );
    }
    
    public static <I> Provider<I> of(ProvidingPoint providingPoint, Class<I> type) {
        return new ProviderImpl<I>(providingPoint, type);
    }
    
    public static <I> void bind(ManagerImpl manager, Context context, ProvidingPoint providingPoint, Class<I> type) {
        if (manager == null) {
            throw new IllegalArgumentException("The parameter [manager] is null.");
        }
        if (context == null) {
            throw new IllegalArgumentException("The parameter [context] is null.");
        }
        manager.bind(type, context, of(providingPoint, type).get());
    }
    
    public I get() {
        return value;
    }
    
}
