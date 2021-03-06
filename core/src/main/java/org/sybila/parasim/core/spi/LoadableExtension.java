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
package org.sybila.parasim.core.spi;

import org.sybila.parasim.core.api.loader.ExtensionBuilder;

/**
 * Classes implemented this interface and mentioned in /META-INF.services/org.sybila.parasim.core.spi.LoadableExtension
 * (full qualified class name per line) are loaded by {@link org.sybila.parasim.core.api.extension.loader.ExtensionLoader}.
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface LoadableExtension {

    /**
     * This method is called when the class is loaded by
     * {@link org.sybila.parasim.core.api.extension.loader.ExtensionBuilder}
     * and provide registering other extensions.
     *
     * @param builder
     */
    void register(ExtensionBuilder builder);

}
