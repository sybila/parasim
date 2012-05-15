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

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.util.Coordinate;
import org.sybila.parasim.util.Pair;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class RobustnessTransformer {

    /**
     * poznámky:
     *  i) je třeba počítat se "vzdálenými sousedy" -- tj. sousedi, kteří jsou dost daleko přes prázdná pole
     *  ii) nelze počítat s tím, že sousedi budou 4 -- v zásadě spíše bude mezi dvěma vrcholy (viz obrázek)
     *  iii) teoreticky by mohl stačit jeden soused (pokud překrývá)
     *  iv) otázkou je, jak do toho zatáhnout další vrstvy -- opět tam nutně nebudou
     *  v) pokud se zatáhnou další vrstvy, nemá cenu vybudovat najednou dvě pole sousedů z vyšších vrstev?
     *         na druhou stranu, pro každý vrchol se asi sousedi budou počítat jednou
     *  vi) v zásadě v "jasných" oblastech nevadí, když se vynechá bod (ZeroRemover)
     *  vii) problémem jsou "prázdné vrstvy", to co vznikne, když se vynechává v projekcích
     *
     * => výsledek -- asi bude nutné brát v úvahu i vyšší vrstvy nebo je sjednocovat
     *
     *
     *
     * Bude fungovat asi takto:
     * pro to, aby se bod dal zařadit by měl stačit jeden pokrývající bod
     *
     * nechť mám od daného bodu bod vzdálený D s robustností R (kladnou)
     * pak daný bod bude mít robustnost alespoň R-D (pokud R>D; podobně pro záporné robustnosti)
     * platí, že všechny body pokryté tímto bodem jsou zároveň pokryté zdrojovým bodem (kružnice)
     *
     * algoritmus tedy projde všechny body, které mají hodnotu robustnosti a v jejich okolí přiřadí
     * hodnoty bodům, které robustnost nemají -- zjistí si, jak široké okolí mají a přiřadí jim hodnoty
     *
     * problém je s jinými vrstvami: pro každý bod projdu všechny osy na oba směry, vezmu nejbližší bod
     * a promítnu ho jako kružnici na vrstvu -- dále postupuju viz výše
     *
     * pro každou kružnici se středem v daném bodě se určí nejzazší bod do obou směrů, pak se projde čtverec
     * a doplní se prázdné body robustností R-D
     *
     * v tomhle případě možná bude chtít mít pole s body, které tam jsou původně a u ostatních zjistit
     * nejvyšší možnou robustnost -- nebo se na to pro tyto účely možná vykašlat.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RobustnessTransformer.class);

    private static class Circle {

        private int x, y;
        private float r;

        public Circle(int x, int y, float robustness) {
            this.x = x;
            this.y = y;
            r = robustness;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public float getRobustness() {
            return r;
        }
    }
    // target //
    private LayeredGrid<Float> src;
    private Float[][] target;
    private int xAxis, yAxis, xSize, ySize;
    private Map<Integer, Integer> projections;
    // auxiliary //
    private Queue<Circle> points;
    private Coordinate.Builder current;

    public RobustnessTransformer(Float[][] target, int xAxis, int yAxis, int xSize, int ySize, Map<Integer, Integer> projections, LayeredGrid<Float> source) {
        this.target = target;
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.xSize = xSize;
        this.ySize = ySize;
        this.projections = projections;
        src = source;

        points = new LinkedList<Circle>();

        int dim = src.getDimension();
        current = new Coordinate.Builder(dim);
        for (int i = 0; i < dim; i++) {
            if (i != xAxis && i != yAxis) {
                current.setCoordinate(i, projections.get(i));
            }
        }
    }

    public void transform() {
        fillQueue();
        while (!points.isEmpty()) {
            processPoint();
        }
    }

    private void fillQueue() {
        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                if (target[i][j] != null) {
                    points.add(new Circle(i, j, target[i][j]));
                } else {
                    //find new robustness in all dimensions
                    Float robustness = findClosestRobustness(i, j);
                    if (robustness != null) {
                        points.add(new Circle(i, j, robustness));
                    }
                }
            }
        }
    }

    /*
     * Finds closest not-null robustness in all dimensions and computes
     * its projection into this dimension.
     *
     * TODO seems very messy -- might be good to refine
     */
    private Float findClosestRobustness(int x, int y) {
        float min = 0;
        Pair<Integer, Integer> minCoord = null;
        float max = 0;
        Pair<Integer, Integer> maxCoord = null;

        current.setCoordinate(xAxis, x).setCoordinate(yAxis, y);

        // for each axe //
        for (int i = 0; i < src.getDimension(); i++) {
            if (i != xAxis && i != yAxis) {
                // up //
                for (int j = projections.get(i); j < src.getDimensions().getCoordinate(i); j++) {
                    Float robustness = src.get(current.setCoordinate(i, j).create());
                    if (robustness != null) {
                        if (robustness < min) {
                            min = robustness;
                            minCoord = new Pair<Integer, Integer>(i, j);
                        } else if (robustness > max) {
                            max = robustness;
                            maxCoord = new Pair<Integer, Integer>(i, j);
                        }
                        break;
                    }
                }
                // down //
                for (int j = projections.get(i); j >= 0; j--) {
                    Float robustness = src.get(current.setCoordinate(i, j).create());
                    if (robustness != null) {
                        if (robustness < min) {
                            min = robustness;
                            minCoord = new Pair<Integer, Integer>(i, j);
                        } else if (robustness > max) {
                            max = robustness;
                            maxCoord = new Pair<Integer, Integer>(i, j);
                        }
                        break;
                    }
                }
                current.setCoordinate(i, projections.get(i));
            }
        }

        float minR = 0;
        float maxR = 0;
        if (minCoord != null) {
            float minD = src.getLayerValue(minCoord.first(), minCoord.second());
            if (minD < -min) {
                minR = (float) -Math.sqrt(Math.pow(min, 2) - Math.pow(minD, 2));
            } else {
                minCoord = null;
            }
        }
        if (maxCoord != null) {
            float maxD = src.getLayerValue(maxCoord.first(), maxCoord.second());
            if (maxD < min) {
                maxR = (float) Math.sqrt(Math.pow(max, 2) - Math.pow(maxD, 2));
            } else {
                maxCoord = null;
            }
        }

        if (minCoord == null && maxCoord == null) {
            return null;
        } else if (minCoord == null) {
            return maxR;
        } else if (maxCoord == null) {
            return minR;
        } else { //should not be possible
            return null;
        }
    }

    /*
     * some refinement might not go amiss
     */
    private void processPoint() {
        Circle point = points.poll();
        float xValue = src.getLayerValue(xAxis, point.getX());
        float yValue = src.getLayerValue(yAxis, point.getY());
        int minX, maxX, minY, maxY;
        for (minX = point.getX() - 1; minX >= 0; minX--) {
            if (Math.abs(xValue - src.getLayerValue(xAxis, minX)) > Math.abs(point.getRobustness())) {
                break;
            }
        }
        minX++;
        for (maxX = point.getX() + 1; maxX < src.getDimensions().getCoordinate(xAxis); maxX++) {
            if (Math.abs(xValue - src.getLayerValue(xAxis, maxX)) > Math.abs(point.getRobustness())) {
                break;
            }
        }
        maxX--;
        for (minY = point.getY() - 1; minY >= 0; minY--) {
            if (Math.abs(yValue - src.getLayerValue(yAxis, minY)) > Math.abs(point.getRobustness())) {
                break;
            }
        }
        minY++;
        for (maxY = point.getY() + 1; maxY < src.getDimensions().getCoordinate(yAxis); maxY++) {
            if (Math.abs(yValue - src.getLayerValue(yAxis, maxY)) > Math.abs(point.getRobustness())) {
                break;
            }
        }
        maxY--;

        for (int i = minX; i < maxX; i++) {
            for (int j = minY; j < maxY; j++) {

                float d = (float) Math.sqrt(Math.pow(src.getLayerValue(xAxis, i) - xValue, 2) + Math.pow(src.getLayerValue(yAxis, j) - yValue, 2));
                if (d < Math.abs(point.getRobustness())) {
                    if (target[i][j] == null) {
                        if (point.getRobustness() < 0) {
                            target[i][j] = point.getRobustness() + d;
                        } else {
                            target[i][j] = point.getRobustness() - d;
                        }
                    } else {
                        if (Math.signum(target[i][j]) != Math.signum(point.getRobustness())) {
                            logOverlappingRobustness(i, j, point);
                        }
                    }
                }
            }
        }
    }

    private void logOverlappingRobustness(int x, int y, Circle point) {
        Point srcPoint = src.getPoint(current.setCoordinate(xAxis, point.getX()).setCoordinate(yAxis, point.getY()).create());
        Point trgPoint = src.getPoint(current.setCoordinate(xAxis, x).setCoordinate(yAxis, y).create());

        StringBuilder warning = new StringBuilder("Overlapping robustness: ");
        warning.append(srcPoint.toString());
        warning.append(", robustness: ");
        warning.append(point.getRobustness());
        warning.append("; ");
        warning.append(trgPoint.toString());
        warning.append(", robustness: ");
        warning.append(target[x][y]);
        warning.append(".");
        LOGGER.warn(warning.toString());
    }

    public static GridPointLayer.SingleLayerFactory getFactory() {
        return new Factory();
    }

    private static class Factory implements GridPointLayer.SingleLayerFactory {

        SimpleSingleLayerFactory init = new SimpleSingleLayerFactory();

        public void transform(Float[][] target, LayeredGrid<Float> source, int xAxis, int yAxis, int xSize, int ySize, Map<Integer, Integer> projections) {
            init.transform(target, source, xAxis, yAxis, xSize, ySize, projections);
            RobustnessTransformer trans = new RobustnessTransformer(target, xAxis, yAxis, xSize, ySize, projections, source);
            trans.transform();
        }
    }
}
