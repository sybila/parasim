/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.visualisation.plot.impl.layer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.verification.result.VerificationResult;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class EpsilonLayering implements Layering {

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
                    layers[dim].add(new IntervalLayer(p.getValue(dim), epsilon));
                }
            }
        }
    }

    private void sortLists(List<Layer>[] layers) {
        for (List<Layer> list : layers) {
            Collections.sort(list, new Layer.Comparator());
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
