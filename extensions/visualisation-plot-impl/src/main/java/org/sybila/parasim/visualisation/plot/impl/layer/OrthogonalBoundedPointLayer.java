package org.sybila.parasim.visualisation.plot.impl.layer;

import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.visualisation.plot.impl.Point2DLayer;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public abstract class OrthogonalBoundedPointLayer implements Point2DLayer {

    private OrthogonalSpace space;

    public OrthogonalBoundedPointLayer(OrthogonalSpace bounds) {
        space = bounds;
    }

    protected OrthogonalSpace getBounds() {
        return space;
    }

    protected abstract int getXAxis();

    protected abstract int getYAxis();

    @Override
    public float minX() {
        return space.getMinBounds().getValue(getXAxis());
    }

    @Override
    public float maxX() {
        return space.getMaxBounds().getValue(getXAxis());
    }

    @Override
    public float minY() {
        return space.getMinBounds().getValue(getYAxis());
    }

    @Override
    public float maxY() {
        return space.getMaxBounds().getValue(getYAxis());
    }
}
