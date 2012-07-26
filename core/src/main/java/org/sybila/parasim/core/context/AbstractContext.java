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
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.core.InstanceStorage;
import org.sybila.parasim.core.MapInstanceStorage;
import org.sybila.parasim.core.spi.InstanceCleaner;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractContext implements Context {

    private boolean activity = false;
    private InstanceStorage instanceStorage;
    private Context parent;

    public AbstractContext(InstanceStorage instanceStorage) {
        if (instanceStorage == null) {
            throw new IllegalArgumentException("The parameter [instanceStorage] is null.");
        }
        this.instanceStorage = instanceStorage;
    }

    public AbstractContext() {
        this(new MapInstanceStorage());
    }

    public static Context of(final Class<? extends Annotation> scope) {
        return of(scope, new MapInstanceStorage());
    }

    public static Context of(final Class<? extends Annotation> scope, final InstanceStorage storage) {
        return new AbstractContext(storage) {
            public Class<? extends Annotation> getScope() {
                return scope;
            }
        };
    }

    public void activate() {
        activity = true;
    }

    @Override
    public void addInstanceCleaner(InstanceCleaner cleaner) {
        this.instanceStorage.addInstanceCleaner(cleaner);
    }

    public void deactivate() {
        activity = false;
    }

    public void destroy() {
        instanceStorage.clear();
        instanceStorage = null;
    }

    public Context getParent() {
        return parent;
    }

    public InstanceStorage getStorage() {
        return instanceStorage;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public boolean isActive() {
        return activity;
    }

    public void setParent(Context context) {
        parent = context;
    }

    public <T> T resolve(Class<T> type, Class<? extends Annotation> qualifier) {
        Validate.notNull(type);
        Validate.notNull(qualifier);
        // the given context has priority
        if (this.isActive()) {
            T value = this.getStorage().get(type, qualifier);
            if (value != null) {
                return value;
            }
        }
        if (this.hasParent()) {
            return this.getParent().resolve(type, qualifier);
        }
        // nothing found
        return null;
    }
}
