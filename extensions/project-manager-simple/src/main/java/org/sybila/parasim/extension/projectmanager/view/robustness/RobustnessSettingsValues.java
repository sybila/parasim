package org.sybila.parasim.extension.projectmanager.view.robustness;

import org.sybila.parasim.extension.projectmanager.model.NamedInitialSampling;
import org.sybila.parasim.extension.projectmanager.model.NamedOrthogonalSpace;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public final class RobustnessSettingsValues {

    private NamedInitialSampling sampling;
    private NamedOrthogonalSpace space;

    public RobustnessSettingsValues(NamedInitialSampling initialSampling, NamedOrthogonalSpace initialSpace) {
        if (!initialSampling.getVariables().equals(initialSpace.getVariables())) {
            throw new IllegalArgumentException("Initial sampling and initial space are inconsistent (different variables).");
        }
        sampling = initialSampling;
        space = initialSpace;
    }

    public NamedInitialSampling getInitialSampling() {
        return sampling;
    }

    public NamedOrthogonalSpace getInitialSpace() {
        return space;
    }
}
