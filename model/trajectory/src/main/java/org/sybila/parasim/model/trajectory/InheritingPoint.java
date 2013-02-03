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
package org.sybila.parasim.model.trajectory;

public class InheritingPoint extends ArrayPoint {

    private final Point parent;

    public InheritingPoint(Point parent, float time, float... data) {
        super(time, data);
        this.parent = parent;
    }

    public InheritingPoint(Point parent, float time, float[] data, int startIndex, int dimension) {
        super(time, data, startIndex, dimension);
        this.parent = parent;
    }

    @Override
    public float getValue(int index) {
        if (index >= super.getDimension()) {
            return parent.getValue(index);
        } else {
            return super.getValue(index);
        }
    }

    @Override
    public int getDimension() {
        return parent.getDimension();
    }

}
