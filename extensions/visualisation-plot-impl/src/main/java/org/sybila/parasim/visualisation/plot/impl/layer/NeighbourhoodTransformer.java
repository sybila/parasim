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
import java.util.List;
import java.util.PriorityQueue;
import org.sybila.parasim.util.Pair;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public abstract class NeighbourhoodTransformer<T> {

    private static class Target<T> {

        private int x, y;
        private T neigh;

        public Target(int x, int y, T neighbourhood) {
            this.x = x;
            this.y = y;
            neigh = neighbourhood;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public T getNeighbourhood() {
            return neigh;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Target)) {
                return false;
            }
            Target<?> target = (Target<?>) obj;
            if (target.getX() != getX()) {
                return false;
            }
            if (target.getY() != getY()) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            return getY() + 59 * getX();
        }
    }
    private Float[][] target;
    private int xSize, ySize;
    private PriorityQueue<Target<T>> unprocessed;
    private Comparator<T> cmp;

    protected abstract T getNeighbourhood(int x, int y);

    protected abstract float computeRobustnes(int x, int y, T neighbourhood);

    protected abstract List<Pair<Integer, Integer>> getNeighbours(int x, int y);

    //comparator has to be "reverse"
    protected NeighbourhoodTransformer(Float[][] target, int xSize, int ySize, Comparator<T> comparator) {
        this.target = target;
        this.xSize = xSize;
        this.ySize = ySize;
        cmp = comparator;
        unprocessed = new PriorityQueue<Target<T>>(11, new Comparator<Target<T>>() {

            public int compare(Target<T> t, Target<T> t1) {
                return cmp.compare(t.getNeighbourhood(), t1.getNeighbourhood());
            }
        });
    }

    protected Float getRobustness(int x, int y) {
        return isInRange(x, y) ? target[x][y] : null;
    }

    public void transform() {
        fillWithEmpty();
        while (!unprocessed.isEmpty()) {
            processPoint();
        }
    }

    private void fillWithEmpty() {
        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                if (target[i][j] == null) {
                    unprocessed.add(new Target<T>(i, j, getNeighbourhood(i, j)));
                }
            }
        }
    }

    private void processPoint() {
        Target<T> point = unprocessed.poll();
        int x = point.getX();
        int y = point.getY();
        target[x][y] = computeRobustnes(x, y, point.getNeighbourhood());
        for (Pair<Integer, Integer> p : getNeighbours(x, y)) {
            updatePoint(p.first(), p.second());
        }
    }

    private void updatePoint(int x, int y) {
        if (isInRange(x, y) && (getRobustness(x, y) == null)) {
            unprocessed.remove(new Target<T>(x, y, null));
            unprocessed.add(new Target<T>(x, y, getNeighbourhood(x, y)));
        }
    }

    private boolean isInRange(int x, int y) {
        return (x >= 0 && x < xSize && y >= 0 && y < ySize);
    }
}
