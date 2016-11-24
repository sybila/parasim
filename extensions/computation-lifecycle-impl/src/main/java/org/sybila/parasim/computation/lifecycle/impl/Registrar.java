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
package org.sybila.parasim.computation.lifecycle.impl;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import org.sybila.parasim.computation.lifecycle.api.ComputationContainer;
import org.sybila.parasim.computation.lifecycle.api.DistributedMemoryExecutor;
import org.sybila.parasim.computation.lifecycle.api.Executor;
import org.sybila.parasim.computation.lifecycle.api.RemoteExecutor;
import org.sybila.parasim.computation.lifecycle.api.SharedMemoryExecutor;
import org.sybila.parasim.computation.lifecycle.impl.common.ComputationLifecycleConfiguration;
import org.sybila.parasim.computation.lifecycle.impl.common.DefaultComputationContainer;
import org.sybila.parasim.computation.lifecycle.impl.distributed.DistributedMemoryExecutorImpl;
import org.sybila.parasim.computation.lifecycle.impl.distributed.RemoteExecutorImpl;
import org.sybila.parasim.computation.lifecycle.impl.shared.SharedMemoryExecutorImpl;
import org.sybila.parasim.core.annotation.Default;
import org.sybila.parasim.core.annotation.Provide;
import org.sybila.parasim.core.api.Context;
import org.sybila.parasim.core.api.Resolver;
import org.sybila.parasim.core.api.configuration.ExtensionDescriptor;
import org.sybila.parasim.core.api.configuration.ExtensionDescriptorMapper;
import org.sybila.parasim.core.api.configuration.ParasimDescriptor;
import org.sybila.parasim.core.api.enrichment.Enrichment;
import org.sybila.parasim.core.impl.remote.HostControlImpl;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class Registrar {

    @Provide
    public ComputationLifecycleConfiguration provideConfiguration(ParasimDescriptor descriptor, ExtensionDescriptorMapper mapper) throws IllegalAccessException {
        ComputationLifecycleConfiguration c = new ComputationLifecycleConfiguration();
        ExtensionDescriptor extensionDescriptor = descriptor.getExtensionDescriptor("computation-lifecycle");
        if (extensionDescriptor != null) {
            mapper.map(extensionDescriptor, c);
        }
        return c;
    }

    @Provide
    public ComputationContainer provideComputationContainer(Resolver resolver) {
        return new DefaultComputationContainer(resolver);
    }

    @Provide
    public SharedMemoryExecutor provideSharedMemoryExecutor(Enrichment enrichment, Context context) {
        return new SharedMemoryExecutorImpl(enrichment, context);
    }

    @Provide
    public DistributedMemoryExecutor provideDistributedMemoryExecutor(Enrichment enrichment, Context context, ComputationLifecycleConfiguration configuration) throws IOException {
        Collection<RemoteExecutor> remoteExecutors = new ArrayList<>();
        for (URI node: configuration.getNodes()) {
            remoteExecutors.add(new HostControlImpl(node).lookup(RemoteExecutor.class, Default.class));
        }
        return new DistributedMemoryExecutorImpl(enrichment, context, remoteExecutors);
    }

    @Provide
    public Executor provideExecutor(Resolver resolver, ComputationLifecycleConfiguration configuration) throws ClassNotFoundException {
        return (Executor) resolver.resolve(configuration.getDefaultExecutor(), Default.class);
    }

    @Provide(immediately = true)
    public RemoteExecutor provideRemoteExecutor(Context context) {
        return new RemoteExecutorImpl(context);
    }
}
