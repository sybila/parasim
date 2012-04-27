package org.sybila.parasim.visualisation.plot.impl.layer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.visualisation.plot.impl.LayerFactory;
import org.sybila.parasim.visualisation.plot.impl.LayerMetaFactory;
import org.sybila.parasim.visualisation.plot.impl.Point2DLayer;
import org.sybila.parasim.visualisation.plot.impl.layer.utils.LayeredGrid;

/*
 * Dostane mřížku -- někde body jsou a někde nejsou.
 * V okamžiku, kdy je jasná mřížka, vrstvu doplní na všechny body (z okolí).
 */
/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class GridPointLayer extends LayeredPointLayer implements LayerMetaFactory, LayerFactory, Point2DLayer {

    public static interface GridFactory {

        public LayeredGrid<Float> getGrid(VerificationResult result, OrthogonalSpace bounds);
    }

    public static interface SingleLayerFactory {

        public void transform(Float[][] target, LayeredGrid<Float> source, int xAxis, int yAxis, int xSize, int ySize, Map<Integer, Integer> projections);
    }
    private SingleLayerFactory factory;
    private LayeredGrid<Float> points;
    private int xAxis, yAxis;
    private Float[][] layer;

    public GridPointLayer(VerificationResult result, OrthogonalSpace bounds, GridFactory gridFactory, SingleLayerFactory layerFactory) {
        super(bounds);
        points = gridFactory.getGrid(result, bounds);
        factory = layerFactory;
    }

    @Override
    protected List<Layer> getLayers(int dim) {
        List<Layer> result = new ArrayList<Layer>();
        for (int i = 0; i < points.getLayers().getSize(dim); i++) {
            result.add(points.getLayers().get(dim, i));
        }
        return result;
    }

    @Override
    protected int getXAxis() {
        return xAxis;
    }

    @Override
    protected int getYAxis() {
        return yAxis;
    }

    //LayerMetaFactory
    @Override
    public LayerFactory getLayerFactory(int xAxis, int yAxis) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        layer = new Float[getXSize()][getYSize()];
        return this;
    }

    private int getXSize() {
        return points.getDimensions().getCoordinate(xAxis);
    }

    private int getYSize() {
        return points.getDimensions().getCoordinate(yAxis);
    }

    private int getXCoord(int index) {
        return index / getYSize();
    }

    private int getYCoord(int index) {
        return index % getYSize();
    }

    //LayerFactory
    @Override
    public Point2DLayer getLayer(Map<Integer, Integer> projections) {
        factory.transform(layer, points, xAxis, yAxis, getXSize(), getYSize(), projections);
        return this;
    }

    @Override
    public int size() {
        return getXSize() * getYSize();
    }

    public float getX(int index) {
        return points.getLayerValue(xAxis, getXCoord(index));
    }

    public float getY(int index) {
        return points.getLayerValue(yAxis, getYCoord(index));
    }

    public float robustness(int index) { //co udělat s null values?
        return layer[getXCoord(index)][getYCoord(index)];
    }
}
