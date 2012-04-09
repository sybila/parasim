package org.sybila.parasim.core.extension.cdi.impl;

import org.sybila.parasim.core.Instance;
import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.ManagerImpl;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.annotations.Observes;
import org.sybila.parasim.core.event.ManagerStarted;
import org.sybila.parasim.core.extension.cdi.api.ServiceFactory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ServiceFactoryRegistrar {

    @Inject
    private Instance<ServiceFactory> serviceFactory;
    @Inject
    private Instance<Manager> manager;

    public void register(@Observes ManagerStarted event) {
        if (!(manager.get() instanceof ManagerImpl)) {
            throw new IllegalStateException("The service factory can be created only with [" + ManagerImpl.class.getName() + "].");
        }
        serviceFactory.set(new ManagedServiceFactory((ManagerImpl) (manager.get())));
    }
}
