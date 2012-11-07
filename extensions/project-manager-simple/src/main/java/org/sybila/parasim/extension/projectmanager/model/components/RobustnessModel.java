package org.sybila.parasim.extension.projectmanager.model.components;

import org.sybila.parasim.computation.density.api.InitialSampling;
import org.sybila.parasim.extension.projectmanager.model.project.ExperimentResourceList;
import org.sybila.parasim.extension.projectmanager.model.project.Project;
import org.sybila.parasim.extension.projectmanager.view.names.NameManagerModel;
import org.sybila.parasim.extension.projectmanager.view.robustness.RobustnessSettingsModel;
import org.sybila.parasim.extension.projectmanager.view.robustness.RobustnessSettingsValues;
import org.sybila.parasim.extension.projectmanager.view.robustness.RobustnessSettingsValuesFactory;
import org.sybila.parasim.model.space.OrthogonalSpace;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class RobustnessModel extends DoubleListNameManagerModel<InitialSampling, OrthogonalSpace, RobustnessSettingsValues> implements RobustnessSettingsModel, NameManagerModel {

    private final RobustnessSettingsValuesFactory factory;
    private final ExperimentModel experiment;

    public RobustnessModel(Project targetProject, ExperimentModel experimentModel) {
        super(targetProject);
        if (experimentModel == null) {
            throw new IllegalArgumentException("Argument (experiment model) is null.");
        }
        factory = new RobustnessSettingsValuesFactory(targetProject.getOdeSystem());
        experiment = experimentModel;
    }

    @Override
    protected ExperimentResourceList<InitialSampling> getFirstList() {
        return getProject().getInitialSamplings();
    }

    @Override
    protected ExperimentResourceList<OrthogonalSpace> getSecondList() {
        return getProject().getInitialSpaces();
    }

    @Override
    protected RobustnessSettingsValues getValue(InitialSampling first, OrthogonalSpace second) {
        return factory.get(first, second);
    }

    @Override
    protected InitialSampling getFirstValue(RobustnessSettingsValues value) {
        return factory.get(value).first();
    }

    @Override
    protected OrthogonalSpace getSecondValue(RobustnessSettingsValues value) {
        return factory.get(value).second();
    }

    @Override
    public void chooseCurrent() {
        checkCurrentName();
        experiment.getRobustnessChooser().chooseName(getCurrentName());
    }

    @Override
    public void valuesChanged(RobustnessSettingsValues values) {
        changeValues(values);
    }
}