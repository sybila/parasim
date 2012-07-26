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
package org.sybila.parasim.core.extension.lifecycle;

import org.sybila.parasim.core.LoadableExtension;
import org.sybila.parasim.core.extension.lifecycle.impl.LifecycleCleaner;
import org.sybila.parasim.core.extension.lifecycle.impl.LifecycleResolver;
import org.sybila.parasim.core.extension.loader.api.ExtensionBuilder;
import org.sybila.parasim.core.spi.DelegatedResolver;
import org.sybila.parasim.core.spi.InstanceCleaner;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class LifecycleExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        builder.service(DelegatedResolver.class, LifecycleResolver.class);
        builder.service(InstanceCleaner.class, LifecycleCleaner.class);
        builder.extension(LifecycleManagingExtension.class);
    }

}
