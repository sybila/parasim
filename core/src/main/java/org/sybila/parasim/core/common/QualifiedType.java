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
package org.sybila.parasim.core.common;

import java.lang.annotation.Annotation;
import java.util.Objects;
import org.sybila.parasim.core.annotation.Default;

public class QualifiedType {

    private final Class<?> type;
    private final Class<? extends Annotation> qualifier;

    public QualifiedType(Class<?> type) {
        this(type, Default.class);
    }

    public QualifiedType(Class<?> type, Class<? extends Annotation> qualifier) {
        this.type = type;
        this.qualifier = qualifier;
    }

    public Class<? extends Annotation> getQualifier() {
        return qualifier;
    }

    public Class<?> getType() {
        return type;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final QualifiedType other = (QualifiedType) obj;
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        if (!Objects.equals(this.qualifier, other.qualifier)) {
            return false;
        }
        return true;
    }
}
