/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
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

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.core.annotation.Application;
import org.sybila.parasim.core.annotation.Observes;
import org.sybila.parasim.core.annotation.Provide;
import org.sybila.parasim.core.api.Resolver;
import org.sybila.parasim.core.api.ServiceRepository;
import org.sybila.parasim.core.api.configuration.ExtensionDescriptor;
import org.sybila.parasim.core.api.configuration.ExtensionDescriptorMapper;
import org.sybila.parasim.core.api.configuration.ParasimDescriptor;
import org.sybila.parasim.core.api.enrichment.Enrichment;
import org.sybila.parasim.core.api.remote.Loader;
import org.sybila.parasim.core.event.ManagerProcessing;
import org.sybila.parasim.core.event.ManagerStarted;
import org.sybila.parasim.core.event.ManagerStopping;
import org.sybila.parasim.core.impl.configuration.ExtensionDescriptorMapperImpl;
import org.sybila.parasim.core.impl.configuration.ParasimDescriptorImpl;
import org.sybila.parasim.core.impl.enrichment.EnrichmentImpl;
import org.sybila.parasim.core.impl.enrichment.InjectingEnricher;
import org.sybila.parasim.core.impl.enrichment.ProvidingEnricher;
import org.sybila.parasim.core.impl.logging.LoggingConfiguration;
import org.sybila.parasim.core.impl.logging.ServiceAppender;
import org.sybila.parasim.core.impl.remote.HostControlImpl;
import org.sybila.parasim.core.impl.remote.LoaderImpl;
import org.sybila.parasim.core.impl.remote.RemoteConfiguration;
import org.sybila.parasim.core.spi.enrichment.Enricher;
import org.xml.sax.SAXException;

@Application
public class CoreExtension {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoreExtension.class);

    private Registry providedRemoteRegistry;

    public void services(@Observes ManagerProcessing event, ServiceStorage serviceStorage) {
        serviceStorage.store(Enricher.class, new InjectingEnricher());
        serviceStorage.store(Enricher.class, new ProvidingEnricher());
    }

    @Provide
    public ParasimDescriptor provideDescriptor() throws IOException, SAXException {
        if (System.getProperty("parasim.config.file") != null && new File(System.getProperty("parasim.config.file")).exists()) {
            return ParasimDescriptorImpl.fromXMLFile("parasim.config.file", "parasim.xml");
        } else {
            return new ParasimDescriptorImpl();
        }
    }

    @Provide
    public Enrichment provideEnrichment(ServiceRepository serviceRepository) {
        return new EnrichmentImpl(serviceRepository);
    }

    @Provide
    public ExtensionDescriptorMapper provideExtensionDescriptorMapper() {
        return new ExtensionDescriptorMapperImpl();
    }

    // logging

    @Provide
    public LoggingConfiguration provideLoggingConfiguration(ParasimDescriptor descriptor, ExtensionDescriptorMapper mapper) throws IllegalAccessException {
        LoggingConfiguration c = new LoggingConfiguration();
        ExtensionDescriptor extensionDescriptor = descriptor.getExtensionDescriptor("logging");
        if (extensionDescriptor != null) {
            mapper.map(extensionDescriptor, c);
        }
        return c;
    }

    public void configureLogging(@Observes ManagerProcessing event, LoggingConfiguration configuration) throws IOException {
        Properties prop = new Properties();
        prop.load(configuration.getConfigFile().openStream());
        PropertyConfigurator.configure(configuration.getConfigFile());
        if (configuration.getLevel() != null && !configuration.getLevel().isEmpty()) {
            prop.setProperty("log4j.rootLogger", configuration.getLevel() + ", stdout, parasim");
        }
        PropertyConfigurator.configure(prop);
        LoggerFactory.getLogger(getClass()).debug("logging configured");
    }

    public void registerLoggingListeners(@Observes ManagerStarted started, ServiceRepository serviceRepository) {
        ServiceAppender.setServiceRepository(serviceRepository);
    }

    // remote

    @Provide(immediately=true)
    public Loader provideLoader(Resolver resolver, RemoteConfiguration configuration) throws RemoteException {
        return new LoaderImpl(resolver, configuration.getPort());
    }

    @Provide
    public RemoteConfiguration provideRemoteConfiguration(ParasimDescriptor descriptor, ExtensionDescriptorMapper mapper) throws IllegalAccessException, UnknownHostException {
        RemoteConfiguration conf = new RemoteConfiguration();
        ExtensionDescriptor extensionDescriptor = descriptor.getExtensionDescriptor("remote");
        if (extensionDescriptor != null) {
            mapper.map(extensionDescriptor, conf);
        }
        return conf;
    }

    @Provide
    public Registry provideRegistry(RemoteConfiguration configuration) throws RemoteException {
        if (System.getSecurityManager() == null) {
            if (System.getProperty("java.security.policy") == null) {
                System.setProperty("java.security.policy", HostControlImpl.SECURITY_POLICY_PATH);
            }
        }
        System.setProperty("java.rmi.server.hostname", configuration.getHost());
        providedRemoteRegistry = LocateRegistry.createRegistry(configuration.getPort());
        LOGGER.info("a new remote registry available on " + configuration.getHost() + ":" + configuration.getPort());
        return providedRemoteRegistry;
    }

    public void cleanRegistry(@Observes ManagerStopping event) {
        if (providedRemoteRegistry != null) {
            try {
                UnicastRemoteObject.unexportObject(providedRemoteRegistry, true);
                LOGGER.debug("RMI registry closed.");
            } catch (RemoteException e) {
                LOGGER.warn("Can't close RMI registry.", e);
            }
        }
    }
}
