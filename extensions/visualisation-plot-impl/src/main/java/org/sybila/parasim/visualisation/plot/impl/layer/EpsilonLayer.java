package org.sybila.parasim.visualisation.plot.impl.layer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.verification.result.VerificationResult;

/**
 * Groups points into layers according to the same coordinates.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class EpsilonLayer extends IndependentLayer {

    private class FloatLayer implements Layer {

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
    private List<Layer>[] layers;

    public EpsilonLayer(float epsilon, VerificationResult result, OrthogonalSpace bounds) {
        super(result, bounds);
        this.epsilon = epsilon;

        int dimension = bounds.getDimension();
        initArray(dimension);
        extractCoordinates(dimension);
        sortLists();
        removeDuplicities();
    }

    private void initArray(int dimension) {
        layers = new List[dimension];
        for (int dim = 0; dim < dimension; dim++) {
            layers[dim] = new ArrayList<Layer>();
        }
    }

    private void extractCoordinates(int dimension) {
        for (int i = 0; i < getResult().size(); i++) {
            Point p = getResult().getPoint(i);
            if (getBounds().isIn(p)) {
                for (int dim = 0; dim < dimension; dim++) {
                    layers[dim].add(new FloatLayer(p.getValue(dim)));
                }
            }
        }
    }

    private void sortLists() {
        for (List<Layer> list : layers) {
            Collections.sort(list, new LayerComparator());
        }
    }

    private void removeDuplicities() {
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

    @Override
    protected List<Layer> getLayers(int index) {
        return layers[index];
    }
}
