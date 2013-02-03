/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.model;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class MergeableBox<T extends Mergeable<T>, L> implements Mergeable<T> {

    private final L load;

    public MergeableBox(L load) {
        if (load == null) {
            throw new IllegalArgumentException("The parameter [load] is null.");
        }
        this.load = load;
    }

    public L get() {
        return load;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MergeableBox<T, L> other = (MergeableBox<T, L>) obj;
        if (this.load != other.load && (this.load == null || !this.load.equals(other.load))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.load != null ? this.load.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return load.toString();
    }
}
