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
}
