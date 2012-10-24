package org.sybila.parasim.extension.projectManager.view;

import org.sybila.parasim.extension.projectManager.model.NamedInitialSampling;
import org.sybila.parasim.extension.projectManager.model.NamedOrthogonalSpace;

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
