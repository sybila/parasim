package org.sybila.parasim.visualisation.plot.impl.layer;

import java.util.ArrayList;
import java.util.Comparator;
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
public class IndependentLayer extends OrthogonalBoundedLayer implements LayerMetaFactory, LayerFactory, Point2DLayer {

    public static interface Layer {

        public boolean isIn(float x);

        public float getValue();
    }

    public static interface Layering {

        public List<Layer>[] computeLayers(VerificationResult result, OrthogonalSpace bounds);
    }

    protected static class LayerComparator implements Comparator<Layer> {

        public int compare(Layer t, Layer t1) {
            return Float.compare(t.getValue(), t1.getValue());
        }
    }
    private VerificationResult result;
    private int xAxis, yAxis;
    private List<float[]> layer;
    private List<Layer>[] layers;

    public IndependentLayer(VerificationResult result, OrthogonalSpace bounds, Layering layering) {
        super(bounds);
        this.result = result;
        layers = layering.computeLayers(result, bounds);
        if (layers.length != bounds.getDimension()) {
            throw new IllegalArgumentException("Layering returned a wrong number of dimensions.");
        }
        layer = new ArrayList<float[]>();
    }

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
    public int ticks(int index) {
        return getLayers(index).size();
    }

    @Override
    public int getTicks(int index, float value) {
        List<Layer> list = getLayers(index);

        //see if it is in layer//
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isIn(value)) {
                return i;
            }
        }

        //is before//
        if (value < list.get(0).getValue()) {
            return 0;
        }

        //is between or at the end//
        int i = 1;
        while ((i < list.size()) && (value < list.get(i).getValue())) {
            i++;
        }
        return i - 1;
    }

    @Override
    public float getValue(int index, int ticks) {
        return getLayers(index).get(ticks).getValue();
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
                layer.add(new float[]{p.getValue(xAxis), p.getValue(yAxis), result.getRobustness(i)});
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
