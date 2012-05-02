package org.sybila.parasim.visualisation.plot.impl.layer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.visualisation.plot.impl.LayerFactory;
import org.sybila.parasim.visualisation.plot.impl.LayerMetaFactory;
import org.sybila.parasim.visualisation.plot.impl.Point2DLayer;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class IndependentPointLayer extends LayeredPointLayer implements LayerMetaFactory, LayerFactory, Point2DLayer {

    private VerificationResult result;
    private int xAxis, yAxis;
    private List<float[]> layer;
    private List<Layer>[] layers;

    public IndependentPointLayer(VerificationResult result, OrthogonalSpace bounds, Layering layering) {
        super(bounds);
        this.result = result;
        layers = layering.computeLayers(result, bounds);
        if (layers.length != bounds.getDimension()) {
            throw new IllegalArgumentException("Layering returned a wrong number of dimensions.");
        }
        layer = new ArrayList<float[]>();
    }

    @Override
    protected List<Layer> getLayers(int index) {
        return layers[index];
    }

    protected VerificationResult getResult() {
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

    //LayerMetaFactory//
    @Override
    public LayerFactory getLayerFactory(int xAxis, int yAxis) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;

        return this;
    }

    @Override
    public Point2DLayer getLayer(Map<Integer, Integer> projections) {
        layer.clear();

        //filter points//
        for (int i = 0; i < result.size(); i++) {
            Point p = result.getPoint(i);

            //has to be in bounds//
            if (!getBounds().isIn(p)) {
                continue;
            }

            //go through all layers//
            int dim;
            for (dim = 0; dim < getBounds().getDimension(); dim++) {
                if ((dim == xAxis) || (dim == yAxis)) { //leave out certain layers
                    continue;
                }
                if (!getLayers(dim).get(projections.get(dim)).isIn(p.getValue(dim))) {
                    break;
                }
            }
            if (dim == getBounds().getDimension()) {
                layer.add(new float[]{p.getValue(xAxis), p.getValue(yAxis), result.getRobustness(i).getValue()});
            }
        }

        return this;
    }

    //Point2DLayer
    @Override
    public int size() {
        return layer.size();
    }

    @Override
    public float getX(int index) {
        return layer.get(index)[0];
    }

    @Override
    public float getY(int index) {
        return layer.get(index)[1];
    }

    @Override
    public float robustness(int index) {
        return layer.get(index)[2];
    }
}
