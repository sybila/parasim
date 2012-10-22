package org.sybila.parasim.extension.projectManager.view;

import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.extension.projectManager.model.NamedOrthogonalSpace;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public final class SimulationSettingsValues {

    private final PrecisionConfiguration precision;
    private final NamedOrthogonalSpace space;
    private final float startTime, endTime;

    public SimulationSettingsValues(PrecisionConfiguration precision, NamedOrthogonalSpace space, float startTime, float endTime) {
        if (precision == null) {
            throw new IllegalArgumentException("Argument (precision) is null.");
        }
        if (space == null) {
            throw new IllegalArgumentException("Argument (space) is null.");
        }
        this.precision = precision;
        this.space = space;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public PrecisionConfiguration getPrecisionConfiguration() {
        return precision;
    }

    public NamedOrthogonalSpace getSimulationSpace() {
        return space;
    }

    public float getSimulationStart() {
        return startTime;
    }

    public float getSimulationEnd() {
        return endTime;
    }
}
