package org.sybila.parasim.computation.density.spawn.cpu;

import org.apache.commons.lang3.Validate;
import org.sybila.parasim.computation.density.api.Configuration;
import org.sybila.parasim.computation.density.api.InitialSampling;
import org.sybila.parasim.model.space.OrthogonalSpace;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractConfiguration implements Configuration {

    private final InitialSampling initialSampling;
    private final OrthogonalSpace initialSpace;

    public AbstractConfiguration(InitialSampling initialSampling, OrthogonalSpace initialSpace) {
        Validate.notNull(initialSampling);
        Validate.notNull(initialSpace);
        this.initialSampling = initialSampling;
        this.initialSpace = initialSpace;
    }

    public InitialSampling getInitialSampling() {
        return initialSampling;
    }

    public OrthogonalSpace getInitialSpace() {
        return initialSpace;
    }

}
