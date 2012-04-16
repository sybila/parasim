package org.sybila.parasim.visualisation.plot.impl.layer;

import java.util.Map;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.visualisation.plot.impl.LayerFactory;
import org.sybila.parasim.visualisation.plot.impl.LayerMetaFactory;
import org.sybila.parasim.visualisation.plot.impl.Point2DLayer;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class OverlapLayer implements Point2DLayer, LayerFactory, LayerMetaFactory {

    private VerificationResult src;
    private OrthogonalSpace space;
    private int xAxis, yAxis;

    public OverlapLayer(VerificationResult source, OrthogonalSpace bounds) {
        src = source;
        space = bounds;
    }

    //LayerMetaFactory
    @Override
    public LayerFactory getLayerFactory(int xAxis, int yAxis) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;

        return this;
    }

    //LayerFactory
    @Override
    public Point2DLayer getLayer(Map<Integer, Integer> projections) {
        return this;
    }

    @Override
    public int ticks(int index) {
        return 10;
    }

    @Override
    public int getTicks(int index, float value) {
        int val = Math.round(value);
        if (val < 0) {
            return 0;
        }
        if (val > 10) {
            return 10;
        }
        return val;
    }

    @Override
    public float getValue(int index, int ticks) {
        return ticks;
    }

    //Point2DLayer
    @Override
    public int size() {
        return src.size();
    }

    @Override
    public float getX(int index) {
        return src.getPoint(index).getValue(xAxis);
    }

    @Override
    public float getY(int index) {
        return src.getPoint(index).getValue(yAxis);
    }

    @Override
    public float robustness(int index) {
        return src.getRobustness(index);
    }

    @Override
    public float minX() {
        return space.getMinBounds().getValue(xAxis);
    }

    @Override
    public float maxX() {
        return space.getMaxBounds().getValue(yAxis);
    }

    @Override
    public float minY() {
        return space.getMinBounds().getValue(yAxis);
    }

    @Override
    public float maxY() {
        return space.getMaxBounds().getValue(yAxis);
    }
}
