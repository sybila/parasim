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
package org.sybila.parasim.visualisation.plot.impl.layer;

import java.util.Comparator;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class SingleLayerFourNeighbourhood {

    private boolean[] neigh;

    public SingleLayerFourNeighbourhood(boolean north, boolean east, boolean south, boolean west) {
        neigh = new boolean[4];
        neigh[0] = north;
        neigh[1] = east;
        neigh[2] = south;
        neigh[3] = west;
    }

    public int size() {
        int result = 0;
        for (boolean dir : neigh) {
            if (dir) {
                result++;
            }
        }
        return result;
    }

    public boolean isNorthSouthWhole() {
        return neigh[0] && neigh[2];
    }

    public boolean isEastWestWhole() {
        return neigh[1] && neigh[3];
    }

    public static Comparator<SingleLayerFourNeighbourhood> getComparator() {
        return new Comparator<SingleLayerFourNeighbourhood>() {

            public int compare(SingleLayerFourNeighbourhood t, SingleLayerFourNeighbourhood t1) {
                if (t.size() == 2 && t1.size() == 2) {
                    boolean tWhole = t.isEastWestWhole() || t.isNorthSouthWhole();
                    boolean t1Whole = t1.isEastWestWhole() || t1.isNorthSouthWhole();
                    if (tWhole && t1Whole) {
                        return 0;
                    } else if (tWhole) {
                        return -1;
                    } else if (t1Whole) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
                return new Integer(t1.size()).compareTo(t.size());
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        if (neigh[0]) {
            result.append("N");
        }
        if (neigh[1]) {
            result.append("E");
        }
        if (neigh[2]) {
            result.append("S");
        }
        if (neigh[3]) {
            result.append("W");
        }
        return result.toString();
    }
}
