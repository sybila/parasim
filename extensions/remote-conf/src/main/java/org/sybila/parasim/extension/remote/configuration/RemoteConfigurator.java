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
package org.sybila.parasim.extension.remote.configuration;

import org.sybila.parasim.core.annotations.Provide;
import org.sybila.parasim.core.extension.configuration.api.ExtensionDescriptor;
import org.sybila.parasim.core.extension.configuration.api.ExtensionDescriptorMapper;
import org.sybila.parasim.core.extension.configuration.api.ParasimDescriptor;

/**
 * @author <a href="mailto:xpapous1@mail.muni.cz">Jan Papousek</a>
 */
public class RemoteConfigurator {

    @Provide
    public RemoteConfiguration provideConfiguration(ParasimDescriptor descriptor, ExtensionDescriptorMapper mapper) throws IllegalAccessException {
        RemoteConfiguration remoteConfiguration = new RemoteConfiguration();
        ExtensionDescriptor extensionDescriptor = descriptor.getExtensionDescriptor("remote");
        if (descriptor != null) {
            mapper.map(extensionDescriptor, remoteConfiguration);
        }
        remoteConfiguration.validate();
        return remoteConfiguration;
    }

}
