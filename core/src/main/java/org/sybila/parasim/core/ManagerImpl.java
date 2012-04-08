package org.sybila.parasim.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.log4j.Logger;
import org.sybila.parasim.core.annotations.ApplicationScope;
import org.sybila.parasim.core.annotations.Scope;
import org.sybila.parasim.core.context.ApplicationContext;
import org.sybila.parasim.core.context.Context;
import org.sybila.parasim.core.event.After;
import org.sybila.parasim.core.event.Before;
import org.sybila.parasim.core.event.ManagerProcessing;
import org.sybila.parasim.core.event.ManagerStarted;
import org.sybila.parasim.core.event.ManagerStopping;
import org.sybila.parasim.core.extension.loader.ExtensionLoaderExtension;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public final class ManagerImpl implements Manager {

    private static final Logger LOGGER = Logger.getLogger(ManagerImpl.class);
    private ApplicationContext applicationContext;
    private Map<Class<? extends Annotation>, Collection<Class<?>>> extensionsByScope;
    private Map<Context, Collection<Extension>> extensionsByContext = new HashMap<Context, Collection<Extension>>();
    
    private ManagerImpl(final Collection<Class<?>> extensionClasses) {
        if (extensionClasses == null) {
            throw new IllegalArgumentException("The parameter [extensionClasses] is null.");
        }
        extensionsByScope = getScopedExtensions(extensionClasses);
    }
    
    public static Manager create() throws Exception {
        return create(ExtensionLoaderExtension.class);
    }
    
    public static Manager create(Class<?>... extensionClasses) throws Exception {
        return create(Arrays.asList(extensionClasses));
    }
    
    public static Manager create(final Collection<Class<?>> extensionClasses) throws Exception {
        // create manager
        ManagerImpl manager = new ManagerImpl(extensionClasses);
        // create application context and prepare its extensions
        manager.applicationContext = new ApplicationContext();
        manager.applicationContext.activate();
        manager.extensionsByContext.put(
            manager.applicationContext,
            manager.createExtensions(manager.extensionsByScope.get(manager.applicationContext.getScope()), manager.applicationContext)
        );
        // fire manager processing
        manager.fireProcessing(manager.applicationContext);
        // load providers
        for (Extension extension: manager.extensionsByContext.get(manager.applicationContext)) {
            for (ProvidingPoint providingPoint: extension.getProvidingPoints()) {
                ProviderImpl.bind(manager, manager.applicationContext, providingPoint, getType(providingPoint.getType()));
            }
        }        
        // fire application context created
        manager.fire(Before.of(manager.applicationContext), manager.applicationContext);
        // add manager as a service
        manager.bind(Manager.class, manager.applicationContext, manager);
        return manager;
    }

    public <T> void bind(final Class<T> type, Context context, T value) {
        context.getStorage().add(type, value);
        fire(value, context);
    }    
    
    public Context getRootContext() {
        return applicationContext;
    }
    
    public void finalizeContext(Context context) {
        finalizeContext(context, true);
    }
    
    public void fire(Object event, Context context) {
        if (event == null) {
            throw new IllegalArgumentException("The parameter [event] is null.");
        }
        if (context == null) {
            throw new IllegalArgumentException("The parameter [context] is null.");
        }
        if (!context.isActive()) {
            throw new IllegalArgumentException("The context is not active, so event can't be fired.");
        }
        for (Extension extension: extensionsByContext.get(context)) {
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

    public void initializeContext(Context context) {
        try {
            context.activate();
            if (!extensionsByContext.containsKey(context)) {
                extensionsByContext.put(context, new ArrayList<Extension>());
            }
            extensionsByContext.get(context).addAll(createExtensions(extensionsByScope.get(context.getScope()), context));
            if (!(context instanceof ApplicationContext)) {
                fire(Before.of(context), applicationContext);
            }
            for (Extension extension: extensionsByContext.get(context)) {
                for (ProvidingPoint providingPoint: extension.getProvidingPoints()) {
                    ProviderImpl.bind(this, context, providingPoint, getType(providingPoint.getType()));
                }
            }
            fire(Before.of(context), context);
        } catch(Exception e) {
            throw new IllegalStateException("The extension for the given context can't be created.", e);
        }
        
    }

    public void inject(Extension extension) {
        // inject fields
        for(InjectionPoint point: extension.getInjectionPoints()) {
            point.set(InstanceImpl.of(getType(point.getType()), extension.getContext(), this));
        }
        // inject events
        for(EventPoint point: extension.getEventPoints()) {
            point.set(EventImpl.of(getType(point.getType()), extension.getContext(), this));
        }
        // inject context events
        for(ContextEventPoint point: extension.getContextEventPoints()) {
            point.set(ContextEventImpl.of((Class<Context>) getType(point.getType()), this, extension.getContext()));
        }
    }

    public <T> T resolve(Class<T> type, Context context) {
        return context.resolve(type);
    }

    public void shutdown() {
        try {
            fire(new ManagerStopping(), applicationContext);
        } finally {
            // destroy contexts (except of application context)
            for (Context context: extensionsByContext.keySet()) {
                if (context instanceof ApplicationContext) {
                    continue;
                }
                finalizeContext(context, false);
            }
            // destroy application context
            finalizeContext(applicationContext);
            // destroy manager
            extensionsByContext.clear();
            extensionsByScope.clear();
        }
    }

    public void start() {
        fire(new ManagerStarted(), applicationContext);
    }

    private void finalizeContext(Context context, boolean remove) {
        if (context.hasParent()) {
            fire(After.of(context), context.getParent());
        }
        fire(After.of(context), context);
        context.destroy();
        extensionsByContext.get(context).clear();
        if (remove) {
            extensionsByContext.remove(context);
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
        
    private Collection<Extension> createExtensions(final Collection<Class<?>> extensionClasses, Context context) throws Exception {
        List<Extension> created = new ArrayList<Extension>();
        for (Class<?> type: extensionClasses) {
            Extension extension = new ExtensionImpl(createInstance(type), context);
            created.add(extension);
            inject(extension);
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
    
    private void fireProcessing(ApplicationContext applicationContext) throws Exception {
        final Collection<Class<?>> newExtensions = new ArrayList<Class<?>>();
        final Collection<Class<? extends Context>> newContexts = new ArrayList<Class<? extends Context>>();
        fire(new ManagerProcessing() {
            public void extension(Class<?> extension) {
                if (extension == null) {
                    throw new IllegalArgumentException("The parameter [extension] is null.");
                }
                newExtensions.add(extension);
            }

            public Manager getManager() {
                return ManagerImpl.this;
            }
        }, applicationContext);
        // load extensions
        Map<Class<? extends Annotation>, Collection<Class<?>>> newExtensionsByScope = getScopedExtensions(newExtensions);
        for(Entry<Class<? extends Annotation>, Collection<Class<?>>> entry: newExtensionsByScope.entrySet()) {
            if (!extensionsByScope.containsKey(entry.getKey())) {
                extensionsByScope.put(entry.getKey(), new ArrayList<Class<?>>());
            }
            extensionsByScope.get(entry.getKey()).addAll(entry.getValue());
        }
        // load extensions to application context
        extensionsByContext.get(applicationContext).addAll(createExtensions(newExtensions, applicationContext));
    }

    private Map<Class<? extends Annotation>, Collection<Class<?>>> getScopedExtensions(final Collection<Class<?>> extensionClasses) {
        Map<Class<? extends Annotation>, Collection<Class<?>>> scopedExtensions = new HashMap<Class<? extends Annotation>, Collection<Class<?>>>();
        for (Class<?> extension: extensionClasses) {
            Class<? extends Annotation> scope = getScope(extension);
            if (!scopedExtensions.containsKey(scope)) {
                scopedExtensions.put(scope, new ArrayList<Class<?>>());
            }
            scopedExtensions.get(scope).add(extension);
        }
        return scopedExtensions;
    }
    
    private Class<? extends Annotation> getScope(Class<?> target) {
        for (Annotation annotation: target.getDeclaredAnnotations()) {
            if (annotation.annotationType().getAnnotation(Scope.class) != null) {
                continue;
            }
            return annotation.annotationType();
        }
        return ApplicationScope.class;
    }

}