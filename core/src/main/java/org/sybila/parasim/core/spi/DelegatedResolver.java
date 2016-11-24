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

import java.lang.annotation.Annotation;
import java.util.Collection;
import org.sybila.parasim.core.api.Binder;
import org.sybila.parasim.core.api.Extension;
import org.sybila.parasim.core.api.Resolver;

/**
 * The delegated resolver is invoked when the manager can't resolve the instance
 * itself. Only the resolver with the maximal precedence is invoked.
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface DelegatedResolver extends Sortable {

    <T> T resolve(Resolver resolver, Binder binder, Collection<Extension> targets, Class<T> type, Class<? extends Annotation> qualifier);

}
