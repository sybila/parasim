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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sybila.parasim.core.extension.loader;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.sybila.parasim.core.Instance;
import org.sybila.parasim.core.LoadableExtension;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.annotations.Observes;
import org.sybila.parasim.core.annotations.Provide;
import org.sybila.parasim.core.event.ManagerStarted;
import org.sybila.parasim.core.extension.cdi.TestServiceFactoryExtension;
import org.sybila.parasim.core.extension.cdi.api.ServiceFactory;
import org.sybila.parasim.core.extension.configuration.api.ExtensionDescriptorMapper;
import org.sybila.parasim.core.extension.configuration.impl.TestExtensionDescriptorMapperImpl;
import org.sybila.parasim.core.extension.interceptor.TestInterceptorExtension;
import org.sybila.parasim.core.extension.loader.api.ExtensionBuilder;
import org.sybila.parasim.core.service.Interceptor;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestedLoadableExtension implements LoadableExtension {

    @Inject
    private Instance<String> toInject;

    public void observesManagerStarted(@Observes ManagerStarted event) {
        TestExtensionLoaderExtension.managerStarted = System.currentTimeMillis();
        toInject.set("HELLO");
    }

    public void observesServiceFactory(@Observes ServiceFactory serviceFactory) {
        TestServiceFactoryExtension.serviceFactory = serviceFactory;
    }

    public void observesExtensionDescriptorMapper(@Observes ManagerStarted event, ExtensionDescriptorMapper mapper) {
        TestExtensionDescriptorMapperImpl.mapper = mapper;
    }

    public void register(ExtensionBuilder builder) {
        try {
            builder.extension(TestedLoadableExtension.class);
            builder.service(Interceptor.class, TestInterceptorExtension.TestInterceptor.class);
            TestExtensionLoaderExtension.extensionRegistered = System.currentTimeMillis();
            Thread.sleep(50);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestedLoadableExtension.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Provide
    public IntegerBox getIntegerBox() {
        return new IntegerBox();
    }

    public static class IntegerBox {
        private Integer inside = 1;

        public java.lang.Integer getInside() {
            return inside;
        }
    }

}