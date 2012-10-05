package org.sybila.parasim.extension.visualisation.projection.model.layers;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.util.Block;
import org.sybila.parasim.visualisation.projection.api.layers.Layer;
import org.sybila.parasim.visualisation.projection.api.layers.Layers;
import org.sybila.parasim.visualisation.projection.api.layers.LayersFactory;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class EpsilonLayersFactory implements LayersFactory {

    private float e;

    private EpsilonLayersFactory(float epsilon) {
        e = epsilon;
    }

    private class LayersComputation {

        private VerificationResult target;
        private int dim;
        private List<SortedSet<Float>> values;
        private Block.Builder<Layer> layers;

        public Layers getResult() {
            return new BlockLayers(layers.create());
        }

        public LayersComputation(VerificationResult target) {
            this.target = target;
            initialize();
            fillValues();
            produceLayers();
        }

        private void initialize() {
            dim = target.getPoint(0).getDimension();
            layers = new Block.Builder<>(dim);
            values = new ArrayList<>(dim);
            for (int i = 0; i < dim; i++) {
                values.add(new TreeSet<Float>());
            }
        }

        private void fillValues() {
            for (int i = 0; i < target.size(); i++) {
                Point p = target.getPoint(i);
                for (int j = 0; i < dim; j++) {
                    values.get(j).add(p.getValue(j));
                }
            }
        }

        private void produceLayers() {
            for (int i = 0; i < dim; i++) {
                greedyReduceValues(values.get(i), layers.getList(i));
            }
        }

        private void greedyReduceValues(SortedSet<Float> values, List<Layer> layers) {
            float firstValue = values.first();
            float lastValue = values.first();
            for (float value : values) {
                if (value > firstValue + 2 * e) {
                    layers.add(createLayer((firstValue + lastValue) / 2));
                } else {
                    lastValue = value;
                }
            }
        }

        private Layer createLayer(float value) {
            return NeighbourhoodLayer.getInstance(value, e);
        }
    }

    @Override
    public Layers getLayers(VerificationResult target) {
        if (target == null) {
            throw new IllegalArgumentException("Verification result cannot be null.");
        }
        if (target.size() == 0) {
            throw new IllegalArgumentException("Verification result is empty.");
        }
        return new LayersComputation(target).getResult();
    }

    public static LayersFactory get(float epsilon) {
        if (epsilon < 0) {
            throw new IllegalArgumentException("Epsilon must be non-negative.");
        }
        return new EpsilonLayersFactory(epsilon);
    }
}
