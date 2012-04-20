package org.sybila.parasim.visualisation.plot.impl.layer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.visualisation.plot.impl.layer.IndependentLayer.Layer;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class EpsilonLayering implements IndependentLayer.Layering {

    private class FloatLayer implements IndependentLayer.Layer {

        private float value;

        public FloatLayer(float value) {
            this.value = value;
        }

        @Override
        public float getValue() {
            return value;
        }

        @Override
        public boolean isIn(float x) {
            return Math.abs(value - x) < epsilon;
        }
    }
    private float epsilon;

    public EpsilonLayering(float epsilon) {
        this.epsilon = epsilon;
    }

    public List<Layer>[] computeLayers(VerificationResult result, OrthogonalSpace bounds) {
        int dimension = bounds.getDimension();

        List<Layer>[] layers = createArray(dimension);
        extractCoordinates(dimension, result, bounds, layers);
        sortLists(layers);
        removeDuplicities(layers);

        return layers;
    }

    private List<Layer>[] createArray(int dimension) {
        List<Layer>[] layers = new List[dimension];
        for (int dim = 0; dim < dimension; dim++) {
            layers[dim] = new ArrayList<Layer>();
        }
        return layers;
    }

    private void extractCoordinates(int dimension, VerificationResult result, OrthogonalSpace bounds, List<Layer>[] layers) {
        for (int i = 0; i < result.size(); i++) {
            Point p = result.getPoint(i);
            if (bounds.isIn(p)) {
                for (int dim = 0; dim < dimension; dim++) {
                    layers[dim].add(new FloatLayer(p.getValue(dim)));
                }
            }
        }
    }

    private void sortLists(List<Layer>[] layers) {
        for (List<Layer> list : layers) {
            Collections.sort(list, new IndependentLayer.LayerComparator());
        }
    }

    private void removeDuplicities(List<Layer>[] layers) {
        for (List<Layer> list : layers) {
            int i = 1;
            while (i < list.size()) {
                if (list.get(i - 1).isIn(list.get(i).getValue())) {
                    list.remove(i);
                } else {
                    i++;
                }
            }
        }
    }
}
