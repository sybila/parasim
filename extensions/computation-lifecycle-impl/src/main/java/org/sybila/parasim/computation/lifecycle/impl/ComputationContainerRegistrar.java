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
package org.sybila.parasim.computation.lifecycle.impl;

import org.sybila.parasim.computation.lifecycle.api.ComputationContainer;
import org.sybila.parasim.computation.lifecycle.api.event.ComputationContainerRegistered;
import org.sybila.parasim.core.ContextEvent;
import org.sybila.parasim.core.Event;
import org.sybila.parasim.core.Instance;
import org.sybila.parasim.core.Manager;
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
    private Instance<Manager> manager;
    @Inject
    private Event<ComputationContainerRegistered> event;
    @Inject
    private ContextEvent<ComputationContext> contextEvent;

    public void register(@Observes ServiceFactoryRegistered event) {
        container.set(new DefaultComputationContainer(serviceFactory.get(), contextEvent));
        this.event.fire(new ComputationContainerRegistered());
    }
}
