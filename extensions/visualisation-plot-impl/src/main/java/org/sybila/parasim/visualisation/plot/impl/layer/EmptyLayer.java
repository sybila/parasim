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
package org.sybila.parasim.visualisation.plot.impl.layer;

import org.sybila.parasim.visualisation.plot.impl.Point2DLayer;

/**
 * @deprecated bogus class
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
@Deprecated
public class EmptyLayer implements Point2DLayer {

    @Override
    public int size() {
        return 0;
    }

    @Override
    public float getX(int index) {
        throw new IllegalArgumentException("Index out of bounds.");
    }

    @Override
    public float getY(int index) {
        throw new IllegalArgumentException("Index out of bounds.");
    }

    public float robustness(int index) {
        throw new IllegalArgumentException("Index out of bounds.");
    }

    @Override
    public float maxX() {
        return 1;
    }

    @Override
    public float minX() {
        return 0;
    }

    @Override
    public float maxY() {
        return 1;
    }

    @Override
    public float minY() {
        return 0;
    }
}
