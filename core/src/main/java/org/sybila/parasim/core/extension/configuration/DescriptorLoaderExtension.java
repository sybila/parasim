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
package org.sybila.parasim.core.extension.configuration;

import java.io.File;
import java.io.IOException;
import org.sybila.parasim.core.Event;
import org.sybila.parasim.core.Instance;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.annotations.Observes;
import org.sybila.parasim.core.event.ManagerProcessing;
import org.sybila.parasim.core.extension.configuration.api.ExtensionDescriptorMapper;
import org.sybila.parasim.core.extension.configuration.api.ParasimDescriptor;
import org.sybila.parasim.core.extension.configuration.api.event.ConfigurationLoaded;
import org.sybila.parasim.core.extension.configuration.impl.ExtensionDescriptorMapperImpl;
import org.sybila.parasim.core.extension.configuration.impl.ParasimDescriptorImpl;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class DescriptorLoaderExtension {

    @Inject
    private Instance<ParasimDescriptor> parasimDescriptor;
    @Inject
    private Instance<ExtensionDescriptorMapper> extensionDescriptorMapper;
    @Inject
    private Event<ConfigurationLoaded> event;

    public void loadDescriptor(@Observes ManagerProcessing event) throws IOException, SAXException {
        if (System.getProperty("parasim.config.file") != null && new File(System.getProperty("parasim.config.file")).exists()) {
            parasimDescriptor.set(ParasimDescriptorImpl.fromXMLFile("parasim.config.file", "parasim.xml"));
        } else {
            parasimDescriptor.set(new ParasimDescriptorImpl());
        }
        extensionDescriptorMapper.set(new ExtensionDescriptorMapperImpl());
        this.event.fire(new ConfigurationLoaded());
    }

}
