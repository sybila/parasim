/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
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
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.spi.LoggingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.core.annotations.ApplicationScope;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.core.annotations.Scope;
import org.sybila.parasim.core.context.ApplicationContext;
import org.sybila.parasim.core.context.Context;
import org.sybila.parasim.core.event.After;
import org.sybila.parasim.core.event.Before;
import org.sybila.parasim.core.event.ManagerProcessing;
import org.sybila.parasim.core.event.ManagerStarted;
import org.sybila.parasim.core.event.ManagerStopping;
import org.sybila.parasim.core.extension.configuration.DescriptorLoaderExtension;
import org.sybila.parasim.core.extension.loader.ExtensionLoaderExtension;
import org.sybila.parasim.core.extension.logging.LoggingExtension;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public final class ManagerImpl implements Manager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagerImpl.class);
    private static final Appender NULL_APPENDER  = new AppenderSkeleton() {
        @Override
        protected void append(LoggingEvent le) {
        }
        public void close() {
        }
        public boolean requiresLayout() {
            return false;
        }
    };
    private ApplicationContext applicationContext;
    private Map<Class<? extends Annotation>, Collection<Class<?>>> extensionsByScope;
    private Map<Context, Collection<Extension>> extensionsByContext = new ConcurrentHashMap<Context, Collection<Extension>>();
    private boolean running = false;

    private ManagerImpl(final Collection<Class<?>> extensionClasses) {
        if (extensionClasses == null) {
            throw new IllegalArgumentException("The parameter [extensionClasses] is null.");
        }
        extensionsByScope = getScopedExtensions(extensionClasses);
    }

    public static Manager create() throws Exception {
        return create(DescriptorLoaderExtension.class, ExtensionLoaderExtension.class, LoggingExtension.class);
    }

    public static Manager create(Class<?>... extensionClasses) throws Exception {
        return create(Arrays.asList(extensionClasses));
    }

    protected static Manager create(final Collection<Class<?>> extensionClasses) throws Exception {
        BasicConfigurator.configure(NULL_APPENDER);
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
        LOGGER.debug("loading loadable extensions");
        // load providers
        for (Extension extension: manager.extensionsByContext.get(manager.applicationContext)) {
            for (ProvidingPoint providingPoint: extension.getProvidingPoints()) {
                ProviderImpl.bind(manager, manager.applicationContext, providingPoint, getType(providingPoint.getType()), providingPoint.getQualifier());
            }
        }
        // fire application context created
        manager.fire(Before.of(manager.applicationContext), manager.applicationContext);
        // add manager as a service
        manager.bind(Manager.class, Default.class, manager.applicationContext, manager);
        return manager;
    }

    public <T> void bind(final Class<T> type, Class<? extends Annotation> qualifier, Context context, T value) {
        Class<? extends Annotation> nonProxyQualifier = (Class<? extends Annotation>) (Proxy.isProxyClass(qualifier) ? qualifier.getInterfaces()[0] : qualifier);
        LOGGER.debug("bind [{}] [{}] as [{}] to context [{}]", new Object[] {type, nonProxyQualifier.getSimpleName(), value.getClass().getName(), context.getClass().getSimpleName() });
        context.getStorage().add(type, nonProxyQualifier, value);
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
                        LOGGER.debug("fire event [{}] to [{}#{}()]", new Object[] {event, extension, method.getMethod().getName()});
                        method.invoke(this, event);
                    } catch(Exception e) {
                        LOGGER.warn("There is an error during firing event ["+event+"] to ["+extension+"#"+method.getMethod().getName()+"()].", e);
                    }
                }
            }
        }
    }

    public void initializeContext(Context context) {
        try {
            context.activate();
            if (!extensionsByContext.containsKey(context)) {
                extensionsByContext.put(context, Collections.synchronizedList(new ArrayList<Extension>()));
            }
            extensionsByContext.get(context).addAll(createExtensions(extensionsByScope.get(context.getScope()), context));
            if (!(context instanceof ApplicationContext)) {
                fire(Before.of(context), applicationContext);
            }
            for (Extension extension: extensionsByContext.get(context)) {
                for (ProvidingPoint providingPoint: extension.getProvidingPoints()) {
                    ProviderImpl.bind(this, context, providingPoint, getType(providingPoint.getType()), providingPoint.getQualifier());
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
            point.set(InstanceImpl.of(getType(point.getType()), point.getQualifier(), extension.getContext(), this));
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

    public boolean isRunning() {
        return running;
    }

    public <T> T resolve(Class<T> type, Class<? extends Annotation> qualifier, Context context) {
        Class<? extends Annotation> nonProxyQualifier = (Class<? extends Annotation>) (Proxy.isProxyClass(qualifier) ? qualifier.getInterfaces()[0] : qualifier);
        T result = context.resolve(type, nonProxyQualifier);
        LOGGER.debug("resolve [{}] [{}] as [{}] in [{}]", new Object[] {type, nonProxyQualifier.getSimpleName(), (result == null ? null : result.getClass()), context.getClass().getSimpleName()});
        return result;
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
            LOGGER.debug("manager shut down");
            running = false;
        }
    }

    public void start() {
        fire(new ManagerStarted(), applicationContext);
        if (LOGGER.isDebugEnabled()) {
            for (Extension extension: extensionsByContext.get(applicationContext)) {
                LOGGER.debug("extension {} loaded to application context",  extension);
            }
            LOGGER.debug("manager started");
        }
        running = true;
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
        if (extensionClasses == null) {
            return created;
        }
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