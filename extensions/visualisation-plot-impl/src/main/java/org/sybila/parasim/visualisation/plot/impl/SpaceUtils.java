package org.sybila.parasim.visualisation.plot.impl;

import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.trajectory.ArrayPoint;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class SpaceUtils {

    private float epsilon;
    private float padding;

    public SpaceUtils(ResultPlotterConfiguration conf) {
        epsilon = conf.getMinimumDifference();
        padding = conf.getFlatDimensionPadding();
    }

    OrthogonalSpace provideWithPadding(OrthogonalSpace src) {
        int dim = src.getDimension();
        float[] upper = new float[dim];
        float[] lower = new float[dim];
        for (int i = 0; i < dim; i++) {
            upper[i] = src.getMaxBounds().getValue(i);
            lower[i] = src.getMinBounds().getValue(i);
            if (Math.abs(upper[i] - lower[i]) < epsilon) { //too close, need padding
                upper[i] += padding;
                lower[i] += padding;
            }
        }

        return new OrthogonalSpace(new ArrayPoint(0, lower, 0, dim), new ArrayPoint(0, upper, 0, dim));
    }
}
