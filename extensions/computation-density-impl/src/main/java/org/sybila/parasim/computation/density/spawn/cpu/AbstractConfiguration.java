package org.sybila.parasim.computation.density.spawn.cpu;

import org.apache.commons.lang3.Validate;
import org.sybila.parasim.computation.density.api.Configuration;
import org.sybila.parasim.computation.density.api.InitialSampling;
import org.sybila.parasim.computation.density.api.PointDistanceMetric;
import org.sybila.parasim.model.space.OrthogonalSpace;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractConfiguration implements Configuration {

    private final PointDistanceMetric distanceMetric;
    private final InitialSampling initialSampling;
    private final OrthogonalSpace initialSpace;

    public AbstractConfiguration(PointDistanceMetric distanceMetric, InitialSampling initialSampling, OrthogonalSpace initialSpace) {
        Validate.notNull(distanceMetric);
        Validate.notNull(initialSampling);
        Validate.notNull(initialSpace);
        this.distanceMetric = distanceMetric;
        this.initialSampling = initialSampling;
        this.initialSpace = initialSpace;
    }

    public PointDistanceMetric getDistanceMetric() {
        return distanceMetric;
    }

    public InitialSampling getInitialSampling() {
        return initialSampling;
    }

    public OrthogonalSpace getInitialSpace() {
        return initialSpace;
    }

}
