/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.core.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.core.annotation.Default;
import org.sybila.parasim.core.api.Binder;
import org.sybila.parasim.core.api.Context;
import org.sybila.parasim.core.api.ContextFactory;
import org.sybila.parasim.core.api.EventDispatcher;
import org.sybila.parasim.core.api.Extension;
import org.sybila.parasim.core.api.ExtensionRepository;
import org.sybila.parasim.core.api.InjectionPoint;
import org.sybila.parasim.core.api.ObservingPoint;
import org.sybila.parasim.core.api.ProvidingPoint;
import org.sybila.parasim.core.api.Resolver;
import org.sybila.parasim.core.api.ServiceRepository;
import org.sybila.parasim.core.api.Typed;
import org.sybila.parasim.core.common.Proxy;
import org.sybila.parasim.core.common.QualifiedType;
import org.sybila.parasim.core.common.ReflectionUtils;
import org.sybila.parasim.core.event.After;
import org.sybila.parasim.core.event.Before;
import org.sybila.parasim.core.spi.DelegatedResolver;

public class ContextImpl implements Context, Binder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContextImpl.class);
    private final InstanceStorage instanceStorage;
    private final ServiceRepository serviceRepository;
    private final Context parent;
    private final ExtensionRepository targetRepository;
    private final Collection<Extension> extensions;
    private final Class<? extends Annotation> scope;

    private ContextImpl(ServiceRepository serviceRepository, ExtensionRepository targetRepository, Context parent, Class<? extends Annotation> scope) {
        this.instanceStorage = new InstanceStorage(serviceRepository);
        this.serviceRepository = serviceRepository;
        this.parent = parent;
        this.scope = scope;
        this.targetRepository = targetRepository;
        this.extensions = targetRepository.extensions(scope);
    }

    /**
     * This method creates and initialize context. The initialization order is:
     * <ul> <li>fire {@link Before} event to the parent context,</li> <li>fire
     * {@link Before} event to the context,</li> <li>bind context services
     * ({@link Context}, {@link Resolver}, {@link EventDispatcher} and
     * {@link ContextFactory},</li> <li>load providers,</li> <li>setup injection
     * points.</li> </ul>
     *
     * @param serviceRepository
     * @param extensionRepository
     * @param parent
     * @param scope
     * @return
     */
    public static ContextImpl of(ServiceRepository serviceRepository, ExtensionRepository extensionRepository, Context parent, Class<? extends Annotation> scope) {
        ContextImpl context = new ContextImpl(serviceRepository, extensionRepository, parent, scope);
        if (parent != null) {
            parent.fire(Before.of(scope));
        }
        context.fire(Before.of(scope));
        context.bind(Context.class, Default.class, context);
        context.bind(Resolver.class, Default.class, context);
        context.bind(EventDispatcher.class, Default.class, context);
        context.bind(ContextFactory.class, Default.class, context);
        // providers
        Map<QualifiedType, ProvidingPoint> immediateProviders = new HashMap<>();
        for (Extension extension : context.extensions) {
            for (ProvidingPoint provider : extension.getProviders()) {
                Class<?> type = ReflectionUtils.getType(provider.getType());
                if ((!type.isInterface() && Modifier.isFinal(type.getModifiers())) || provider.immediately()) {
                    immediateProviders.put(new QualifiedType(type, provider.getQualifier()), provider);
                } else {
                    try {
                        context.bindUnsafely(type, provider.getQualifier(), Proxy.of(type, new ProvidingMethodHandler(context, provider)));
                    } catch (Exception e) {
                        throw new IllegalStateException("Can't bind " + type.getName() + " with " + provider.getQualifier().getSimpleName() + " qualifier.", e);
                    }
                }
            }
        }
        // immediate providers
        while (!immediateProviders.isEmpty()) {
            Map<QualifiedType, ProvidingPoint> newImmediateProviders = new HashMap<>();
            for (Entry<QualifiedType, ProvidingPoint> provider: immediateProviders.entrySet()) {
                boolean valid = true;
                for (Typed dependency: provider.getValue().dependencies()) {
                    if (context.resolve(ReflectionUtils.getType(dependency.getType()), dependency.getQualifier()) == null) {
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    Object value = provider.getValue().value(context);
                    if (value != null) {
                        context.bindUnsafely(provider.getKey().getType(), provider.getValue().getQualifier(), value);
                    } else {
                        if (provider.getValue().required()) {
                            throw new IllegalStateException("The providing value of required providing point " + provider.getValue() + " is null.");
                        }
                    }
                } else {
                    newImmediateProviders.put(provider.getKey(), provider.getValue());
                }
            }
            if (immediateProviders.size() == newImmediateProviders.size()) {
                for (Entry<QualifiedType, ProvidingPoint> provider: immediateProviders.entrySet()) {
                    Object value = provider.getValue().value(context);
                    if (value != null) {
                        context.bindUnsafely(provider.getKey().getType(), provider.getValue().getQualifier(), value);
                    } else {
                        if (provider.getValue().required()) {
                            throw new IllegalStateException("The providing value of required providing point " + provider.getValue() + " is null.");
                        }
                    }
                }
            }
            immediateProviders.clear();
        }
        // injections
        for (Extension extension : context.extensions) {
            for (InjectionPoint injection : extension.getInjections()) {
                Object value = context.resolve(ReflectionUtils.getType(injection.getType()), injection.getQualifier());
                if (value != null) {
                    injection.set(value);
                } else {
                    if (injection.required()) {
                        throw new IllegalStateException("There is no value for required injection point " + injection + ".");
                    }
                }
            }
        }
        // log
        if (LOGGER.isDebugEnabled()) {
            for (Extension extension : context.extensions) {
                LOGGER.debug("extension " + extension + " loaded to " + scope.getSimpleName() + " scope");
            }
        }
        return context;
    }

    @Override
    public Context getParent() {
        return parent;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return scope;
    }

    @Override
    public boolean hasParent() {
        return parent != null;
    }

    @Override
    public synchronized void destroy() throws Exception {
        fire(After.of(getScope()));
        if (parent != null) {
            parent.fire(After.of(getScope()));
        }
        instanceStorage.clear(this);
        extensions.clear();
    }

    @Override
    public <T> T resolve(Class<T> type, Class<? extends Annotation> qualifier) {
        Class<? extends Annotation> nonProxyQualifier = ReflectionUtils.clean(qualifier);
        T result = instanceStorage.get(type, nonProxyQualifier);
        if (result == null) {
            Collection<DelegatedResolver> resolvers = serviceRepository.service(DelegatedResolver.class);
            for (DelegatedResolver resolver : resolvers) {
                result = resolver.resolve(this, this, extensions, type, nonProxyQualifier);
                if (result != null) {
                    return result;
                }
            }
        } else {
            return result;
        }
        if (hasParent()) {
            return getParent().resolve(type, nonProxyQualifier);
        } else {
            return null;
        }
    }

    @Override
    public void fire(Object event) {
        for (Extension target : extensions) {
            for (ObservingPoint observer : target.getObservers()) {
                if (ReflectionUtils.getType(observer.getType()).isAssignableFrom(event.getClass())) {
                    try {
                        observer.observe(this, event);
                    } catch (Exception e) {
                        LOGGER.warn("There is an error during firing event [" + event + "] to [" + observer + "].", e);
                    }
                }
            }
        }
    }

    @Override
    public Context context(Class<? extends Annotation> scope) {
        return ContextImpl.of(serviceRepository, targetRepository, this, scope);
    }

    @Override
    public <T> void bind(Class<T> type, Class<? extends Annotation> qualifier, T instance) {
        instanceStorage.add(type, qualifier, instance);
        fire(instance);
    }

    protected <T> void bindUnsafely(Class<T> type, Class<? extends Annotation> qualifer, Object instance) {
        bind(type, qualifer, type.cast(instance));
    }
}
