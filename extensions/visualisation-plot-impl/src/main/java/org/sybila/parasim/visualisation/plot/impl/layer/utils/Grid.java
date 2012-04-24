package org.sybila.parasim.visualisation.plot.impl.layer.utils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class Grid<T> {

    private Coordinate dimension;
    private Map<Coordinate, T> grid;

    public Grid(Coordinate dimension) {
        grid = new HashMap<Coordinate, T>();
        this.dimension = dimension;
    }

    public int getDimension() {
        return dimension.getDimension();
    }

    public Coordinate getDimensions() {
        return dimension;
    }

    public T get(Coordinate coord) {
        checkDimension(coord);
        return grid.get(coord);
    }

    public T put(Coordinate coord, T target) {
        checkDimension(coord);
        return grid.put(coord, target);
    }

    protected void checkDimension(Coordinate coord) {
        if (coord.getDimension() != dimension.getDimension()) {
            throw new IllegalArgumentException("Input coordinate has wrong dimension: " + coord.getDimension() + " (expected " + dimension.getDimension() + ")");
        }
    }
}
