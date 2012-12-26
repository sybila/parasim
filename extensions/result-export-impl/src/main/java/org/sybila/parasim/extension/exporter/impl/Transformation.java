package org.sybila.parasim.extension.exporter.impl;

import org.sybila.parasim.model.space.OrthogonalSpace;

public class Transformation {

    private final OrthogonalSpace space;
    private final int size;

    public Transformation(OrthogonalSpace space, int size) {
        this.space = space;
        this.size = size;
    }

    public int transform(int dimension, float coord) {
        double unit = Math.floor(size / (space.getMaxBounds().getValue(dimension) - space.getMinBounds().getValue(dimension)));
        double moved = coord - space.getMinBounds().getValue(dimension);
        return (int) Math.round(unit * moved);
    }

}
