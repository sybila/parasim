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
package org.sybila.parasim.util;

import java.io.Serializable;

/**
 * Pair of objects of different type. Immutable (references to contained objects cannot be changed).
 *
 * Two pairs are equal if and only if both contained objects are equal (respectively).
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 * @param F type of first object.
 * @param S type of second object.
 */
public class Pair<F, S> implements Serializable {

    private F fst;
    private S snd;

    /**
     * Specifies both objects.
     * @param first First object.
     * @param second Second object.
     */
    public Pair(F first, S second) {
        fst = first;
        snd = second;
    }

    /**
     * Projection to first component.
     * @return First contained object.
     */
    public F first() {
        return fst;
    }

    /**
     * Projection to the second component.
     * @return Second contained object.
     */
    public S second() {
        return snd;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pair)) {
            return false;
        }
        Pair<?, ?> target = (Pair<?, ?>) obj;
        return first().equals(target.first()) && second().equals(target.second());
    }

    @Override
    public int hashCode() {
        return first().hashCode() + 43 * second().hashCode();
    }
}
