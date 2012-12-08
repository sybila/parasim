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
package org.sybila.parasim.util;

import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.Validate;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class Iterables {

    private Iterables() {
    }

    public static <T> Iterable<T> concat(final List<Iterable<T>> iterables) {
        Validate.notNull(iterables);
        if (iterables.isEmpty()) {
            throw new IllegalArgumentException("At least one iterable has to be given.");
        }
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {

                    private int currentIndex = 0;

                    private Iterator<T> currentIterator = iterables.get(0).iterator();

                    @Override
                    public boolean hasNext() {
                        if (currentIterator.hasNext()) {
                            return true;
                        }
                        while (currentIndex < iterables.size() - 1) {
                            currentIndex++;
                            currentIterator = iterables.get(currentIndex).iterator();
                            if (currentIterator.hasNext()) {
                                return true;
                            }
                        }
                        return false;
                    }

                    @Override
                    public T next() {
                        if (hasNext()) {
                            return currentIterator.next();
                        }
                        return null;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                };
            }
        };
    }

    public static <T> Iterable<T> concat(final Iterable<T>...iterables) {
        Validate.notNull(iterables);
        if (iterables.length == 0) {
            throw new IllegalArgumentException("At least one iterable has to be given.");
        }
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {

                    private int currentIndex = 0;

                    private Iterator<T> currentIterator = iterables[0].iterator();

                    @Override
                    public boolean hasNext() {
                        if (currentIterator.hasNext()) {
                            return true;
                        }
                        while (currentIndex < iterables.length-1) {
                            currentIndex++;
                            currentIterator = iterables[currentIndex].iterator();
                            if (currentIterator.hasNext()) {
                                return true;
                            }
                        }
                        return false;
                    }

                    @Override
                    public T next() {
                        if (hasNext()) {
                            return currentIterator.next();
                        }
                        return null;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                };
            }
        };
    }

}
