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
package org.sybila.parasim.core.context;

import java.lang.annotation.Annotation;
import org.sybila.parasim.core.InstanceStorage;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Context {

    void activate();

    void deactivate();

    void destroy();

    Context getParent();

    Class<? extends Annotation> getScope();

    InstanceStorage getStorage();

    boolean hasParent();

    boolean isActive();

    void setParent(Context context);

    <T> T resolve(Class<T> type, Class<? extends Annotation> qualifier);
}
