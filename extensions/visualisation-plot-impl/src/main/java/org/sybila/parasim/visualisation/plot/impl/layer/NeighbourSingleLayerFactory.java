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
package org.sybila.parasim.visualisation.plot.impl.layer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
@Deprecated
public class NeighbourSingleLayerFactory implements GridPointLayer.SingleLayerFactory {

    private static class TargetPoint {

        private int x, y, n;

        public TargetPoint(int x, int y, int n) {
            this.x = x;
            this.y = y;
            this.n = n;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getN() {
            return n;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TargetPoint)) {
                return false;
            }
            TargetPoint target = (TargetPoint) obj;
            if (getX() != target.getX()) {
                return false;
            }
            if (getY() != target.getY()) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            return getX() + 41 * getY();
        }
    }

    private static class NeighbourComparator implements Comparator<TargetPoint> {

        public int compare(TargetPoint t, TargetPoint t1) {
            return -(new Integer(t.getN()).compareTo(t1.getN()));
        }
    }
    private SimpleSingleLayerFactory initial = new SimpleSingleLayerFactory();

    public void transform(Float[][] target, LayeredGrid<Float> source, int xAxis, int yAxis, int xSize, int ySize, Map<Integer, Integer> projections) {
        // fill basic grid //
        initial.transform(target, source, xAxis, yAxis, xSize, ySize, projections);
        Transformator trans = new Transformator(target, xSize, ySize, projections);
        trans.transform();
    }

    private static class Transformator {

        private Float[][] target;
        private int xSize, ySize;
        PriorityQueue<TargetPoint> unprocessed;

        public Transformator(Float[][] target, int xSize, int ySize, Map<Integer, Integer> projections) {
            this.target = target;
            this.xSize = xSize;
            this.ySize = ySize;


            unprocessed = new PriorityQueue<TargetPoint>(11, new NeighbourComparator()); //what initial capacity??
        }

        public void transform() {
            fillEmpty();
            while (!unprocessed.isEmpty()) {
                processPoint();
            }
        }

        private void fillEmpty() {
            for (int i = 0; i < xSize; i++) {
                for (int j = 0; j < ySize; j++) {
                    if (target[i][j] == null) {
                        unprocessed.add(new TargetPoint(i, j, neighbourNumber(i, j)));
                    }
                }
            }
        }

        private void processPoint() {
            TargetPoint point = unprocessed.poll();
            int x = point.getX();
            int y = point.getY();
            List<Float> neighbours = new ArrayList<Float>();
            addNotNull(neighbours, getRobustness(x - 1, y));
            addNotNull(neighbours, getRobustness(x + 1, y));
            addNotNull(neighbours, getRobustness(x, y - 1));
            addNotNull(neighbours, getRobustness(x, y + 1));
            float robustness = 0;
            for (Float f : neighbours) {
                robustness += f;
            }
            robustness /= neighbours.size();
            target[x][y] = robustness;

            updateNeighbours(x, y);
        }

        private void updateNeighbours(int x, int y) {
            if ((x > 0) && (target[x - 1][y] == null)) {
                updatePoint(x - 1, y);
            }
            if ((x < xSize - 1) && (target[x + 1][y] == null)) {
                updatePoint(x + 1, y);
            }
            if ((y > 0) && (target[x][y - 1] == null)) {
                updatePoint(x, y - 1);
            }
            if ((y < ySize - 1) && (target[x][y + 1] == null)) {
                updatePoint(x, y + 1);
            }
        }

        private void updatePoint(int x, int y) {
            unprocessed.remove(new TargetPoint(x, y, 0));
            unprocessed.add(new TargetPoint(x, y, neighbourNumber(x, y)));
        }

        private int neighbourNumber(int x, int y) {
            int result = 0;
            if ((x > 0) && (target[x - 1][y] != null)) {
                result++;
            }
            if ((x < xSize - 1) && (target[x + 1][y] != null)) {
                result++;
            }
            if ((y > 0) && (target[x][y - 1] != null)) {
                result++;
            }
            if ((y < ySize - 1) && (target[x][y + 1] != null)) {
                result++;
            }
            return result;
        }

        private static <T> void addNotNull(List<T> list, T target) {
            if (target != null) {
                list.add(target);
            }
        }

        private Float getRobustness(int x, int y) {
            if ((x < 0) || (x >= xSize)) {
                return null;
            }
            if ((y < 0) || (y >= ySize)) {
                return null;
            }
            return target[x][y];
        }
    }
}
