package org.sybila.parasim.computation.lifecycle.impl;

import org.sybila.parasim.computation.lifecycle.api.ComputationContainer;
import org.sybila.parasim.computation.lifecycle.api.event.ComputationContainerRegistered;
import org.sybila.parasim.core.Event;
import org.sybila.parasim.core.Instance;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.annotations.Observes;
import org.sybila.parasim.core.extension.cdi.api.ServiceFactory;
import org.sybila.parasim.core.extension.cdi.api.event.ServiceFactoryRegistered;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ComputationContainerRegistrar {
    
    @Inject 
    private Instance<ServiceFactory> serviceFactory;
    @Inject
    private Instance<ComputationContainer> container;
    @Inject
    private Event<ComputationContainerRegistered> event;
    
    public void register(@Observes ServiceFactoryRegistered event) {
        container.set(new DefaultComputationContainer(serviceFactory.get()));
        this.event.fire(new ComputationContainerRegistered());
    }
    
}
