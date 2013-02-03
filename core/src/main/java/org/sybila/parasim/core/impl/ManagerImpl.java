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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.spi.LoggingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.core.annotation.Application;
import org.sybila.parasim.core.annotation.Default;
import org.sybila.parasim.core.api.Binder;
import org.sybila.parasim.core.api.Context;
import org.sybila.parasim.core.api.ContextFactory;
import org.sybila.parasim.core.api.EventDispatcher;
import org.sybila.parasim.core.api.Extension;
import org.sybila.parasim.core.api.ExtensionRepository;
import org.sybila.parasim.core.api.Manager;
import org.sybila.parasim.core.api.Resolver;
import org.sybila.parasim.core.api.ServiceRepository;
import org.sybila.parasim.core.api.loader.ExtensionBuilder;
import org.sybila.parasim.core.api.loader.ExtensionLoader;
import org.sybila.parasim.core.event.ManagerProcessing;
import org.sybila.parasim.core.event.ManagerStarted;
import org.sybila.parasim.core.event.ManagerStopping;
import org.sybila.parasim.core.impl.loader.ExtensionBuilderImpl;
import org.sybila.parasim.core.impl.loader.SPIExtensionLoader;
import org.sybila.parasim.core.spi.LoadableExtension;

public class ManagerImpl implements Manager, Binder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagerImpl.class);

    private final ContextImpl context;
    private final ServiceStorage serviceStorage;
    private final ExtensionStorage extensionStorage;
    private boolean running = false;

    private static final Appender NULL_APPENDER = new AppenderSkeleton() {
        @Override
        protected void append(LoggingEvent le) {
        }
        @Override
        public void close() {
        }
        @Override
        public boolean requiresLayout() {
            return false;
        }
    };

    public ManagerImpl(ContextImpl context, ServiceStorage serviceStorage, ExtensionStorage extensionStorage) {
        this.context = context;
        this.serviceStorage = serviceStorage;
        this.extensionStorage = extensionStorage;
    }

    public static Manager create(Class<?>... extensionClasses) throws Exception {
        return create(new SPIExtensionLoader(), extensionClasses);
    }

    public static Manager create(ExtensionLoader extensionLoader, Class<?>... extensionClasses) throws Exception {
        List<Class<?>> classes = new ArrayList<>(Arrays.asList(extensionClasses));
        classes.add(CoreExtension.class);
        return create(extensionLoader, classes);
    }

    protected static Manager create(ExtensionLoader extensionLoader, Collection<Class<?>> extensionClasses) throws Exception {
        BasicConfigurator.configure(NULL_APPENDER);
        ServiceStorage serviceStorage = new ServiceStorage();
        ExtensionStorage extensionStorage = new ExtensionStorage();
        // load extensions
        for (Class<?> extensionClass: extensionClasses) {
            extensionStorage.store(extensionClass);
        }
        ExtensionBuilder builder = new ExtensionBuilderImpl(serviceStorage, extensionStorage);
        for (LoadableExtension loadable: extensionLoader.load()) {
            loadable.register(builder);
        }
        // create context and manager
        ContextImpl root = ContextImpl.of(serviceStorage, extensionStorage, null, Application.class);
        ManagerImpl manager = new ManagerImpl(root, serviceStorage, extensionStorage);
        // API services
        manager.bind(Manager.class, Default.class, manager);
        manager.bind(Context.class, Default.class, manager);
        manager.bind(ServiceRepository.class, Default.class, manager);
        manager.bind(ExtensionRepository.class, Default.class, manager);
        manager.bind(Resolver.class, Default.class, manager);
        manager.bind(EventDispatcher.class, Default.class, manager);
        manager.bind(ContextFactory.class, Default.class, manager);
        // IMPL dependent services
        manager.bind(ServiceStorage.class, Default.class, serviceStorage);
        manager.bind(ExtensionStorage.class, Default.class, extensionStorage);
        // event
        manager.fire(new ManagerProcessing());
        return manager;
    }

    @Override
    public synchronized void start() {
        this.running = true;
        fire(new ManagerStarted());
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    ManagerImpl.this.destroy();
                } catch (Exception e) {
                    LOGGER.error("Can't destroy manager", e);
                }
            }
        });

    }

    @Override
    public Context getParent() {
        return null;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return Application.class;
    }

    @Override
    public boolean hasParent() {
        return false;
    }

    @Override
    public synchronized void destroy() throws Exception {
        if (!this.running) {
            return;
        }
        this.running = false;
        try {
            fire(new ManagerStopping());
        } finally {
            context.destroy();
        }
    }

    @Override
    public <T> T resolve(Class<T> type, Class<? extends Annotation> qualifier) {
        return context.resolve(type, qualifier);
    }

    @Override
    public void fire(Object event) {
        context.fire(event);
    }

    @Override
    public Context context(Class<? extends Annotation> scope) {
        return context.context(scope);
    }

    @Override
    public <T> Collection<T> service(Class<T> serviceClass) {
        return serviceStorage.load(serviceClass);
    }

    @Override
    public Collection<Extension> extensions(Class<? extends Annotation> scope) {
        return extensionStorage.load(scope);
    }

    @Override
    public <T> void bind(Class<T> type, Class<? extends Annotation> qualifier, T instance) {
        context.bind(type, qualifier, instance);
    }

}
