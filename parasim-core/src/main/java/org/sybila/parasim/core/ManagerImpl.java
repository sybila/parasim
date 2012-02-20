package org.sybila.parasim.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.sybila.parasim.core.annotations.ApplicationScope;
import org.sybila.parasim.core.context.Context;
import org.sybila.parasim.core.event.ManagerProcessing;
import org.sybila.parasim.core.event.ManagerStarted;
import org.sybila.parasim.core.event.ManagerStopping;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public final class ManagerImpl implements Manager {

    private Map<Class<? extends Annotation>, Context> contexts;
    private Collection<Extension> extensions;
    private static final Logger LOGGER = Logger.getLogger(ManagerImpl.class);
    
    public ManagerImpl(Collection<Class<? extends Context>> contextClasses, final Collection<Class<?>> extensionClasses) {
        if (extensionClasses == null) {
            throw new IllegalArgumentException("The paramater [extensionClasses] is null.");
        }
        if (contextClasses == null) {
            throw new IllegalArgumentException("The paramater [contextClasses] is null.");
        }
        try {
            extensions = createExtensions(extensionClasses);
            contexts = createContexts(contextClasses);
            if (!contexts.containsKey(ApplicationScope.class)) {
                throw new IllegalStateException("There is no context for application scope.");
            }
            fireProcessing();
            contexts.get(ApplicationScope.class).activate();
            for (Context context: contexts.values()) {
                contexts.get(ApplicationScope.class).getStorage().add(
                    (Class<Context>) context.getClass(),
                    context
                );
            }
            for(Extension extension: extensions) {
                inject(extension);
            }
        } catch (Exception ex) {
            throw new IllegalStateException("The manager can't be created.", ex);
        }
        bind(Manager.class, ApplicationScope.class, this);
    }
    
    public void fire(Object event) {
        if (event == null) {
            throw new IllegalArgumentException("The parameter [event] is null.");
        }
        for (Extension extension: extensions) {
            for (ObserverMethod method: extension.getObservers()) {
                if (getType(method.getType()).isAssignableFrom(event.getClass())) {
                    try {
                        method.invoke(this, event);
                    } catch(Exception e) {
                        LOGGER.warn("There is an error during firing event.", e);
                    }
                }
            }
        }
    }

    public void inject(Object o) {
        inject(new ExtensionImpl(o));
    }

    public <T> T resolve(final Class<T> type) {
        for (Context context: contexts.values()) {
            if (!context.isActive()) {
                continue;
            }
            T value = context.getStorage().get(type);
            if (value != null) {
                return value;
            }
        }
        return null;
    }
    
    public <T> void bind(final Class<T> type, Class<? extends Annotation> scope, T value) {
        contexts.get(scope).getStorage().add(type, value);
        fire(value);
    }
    
    public void shutdown() {
        try {
            fire(new ManagerStopping());
        } finally {
            for (Context context: contexts.values()) {
                try {
                    context.destroy();
                } catch(Exception e) {
                    LOGGER.error("The context can't be destroyed.", e);
                }
                contexts.clear();
            }
            extensions.clear();
        }
        
    }

    public void start() {
        fire(new ManagerStarted());
    }
    
    private Collection<Extension> createExtensions(final Collection<Class<?>> extensionClasses) throws Exception {
        List<Extension> created = new ArrayList<Extension>();
        for (Class<?> type: extensionClasses) {
            created.add(new ExtensionImpl(createInstance(type)));
        }
        return created;
    }
    
    private Map<Class<? extends Annotation>, Context> createContexts(final Collection<Class<? extends Context>> contextClasses) throws Exception {
        Map<Class<? extends Annotation>, Context> created = new HashMap<Class<? extends Annotation>, Context>();
        for (Class<? extends Context> type: contextClasses) {
            Context context = createInstance(type);
            created.put(context.getScope(), context);
        }
        return created;
    }
    
    private <T> T createInstance(Class<T> type) throws Exception {
        for (Constructor<?> constructor: type.getDeclaredConstructors()) {
            if (constructor.getParameterTypes().length == 0) {
               if (!constructor.isAccessible()) {
                   constructor.setAccessible(true);
               }
               return (T) constructor.newInstance();
            }
        }
        throw new InvocationException("There is no empty constructor in class " + type.getName());
    }
    
    private void inject(Extension extension) {
        // inject fields
        for(InjectionPoint point: extension.getInjectionPoints()) {
            point.set(InstanceImpl.of(getType(point.getType()), point.getScope(), this));
        }
        // inject events
        for(EventPoint point: extension.getEventPoints()) {
            point.set(EventImpl.of(getType(point.getType()), this));
        }        
    }
    
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

    private void fireProcessing() throws Exception {
        final Collection<Class<?>> newExtensions = new ArrayList<Class<?>>();
        final Collection<Class<? extends Context>> newContexts = new ArrayList<Class<? extends Context>>();
        fire(new ManagerProcessing() {
            public void context(Class<? extends Context> context) {
                if (context == null) {
                    throw new IllegalArgumentException("The parameter [context] is null.");
                }
                newContexts.add(context);
            }

            public void extension(Class<?> extension) {
                if (extension == null) {
                    throw new IllegalArgumentException("The parameter [extension] is null.");
                }
                newExtensions.add(extension);
            }

            public Manager getManager() {
                return ManagerImpl.this;
            }
        });
        extensions.addAll(createExtensions(newExtensions));
        for (Context context: createContexts(newContexts).values()) {
            if (contexts.containsKey(context.getScope())) {
                throw new IllegalStateException("The context for the scope [" + context.getScope().getName() + " already exists.");
            }
            contexts.put(context.getScope(), context);
        }
    }

}
