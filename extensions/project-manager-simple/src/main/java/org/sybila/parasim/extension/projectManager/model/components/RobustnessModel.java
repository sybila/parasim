package org.sybila.parasim.extension.projectManager.model.components;

import org.sybila.parasim.computation.density.api.InitialSampling;
import org.sybila.parasim.extension.projectManager.model.project.Project;
import org.sybila.parasim.extension.projectManager.model.project.ResourceList;
import org.sybila.parasim.extension.projectManager.view.names.NameManagerModel;
import org.sybila.parasim.extension.projectManager.view.robustness.RobustnessSettingsModel;
import org.sybila.parasim.extension.projectManager.view.robustness.RobustnessSettingsValues;
import org.sybila.parasim.extension.projectManager.view.robustness.RobustnessSettingsValuesFactory;
import org.sybila.parasim.model.space.OrthogonalSpace;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class RobustnessModel extends DoubleListNameManagerModel<InitialSampling, OrthogonalSpace, RobustnessSettingsValues> implements RobustnessSettingsModel, NameManagerModel {

    private final RobustnessSettingsValuesFactory factory;

    public RobustnessModel(Project targetProject) {
        super(targetProject);
        factory = new RobustnessSettingsValuesFactory(targetProject.getOdeSystem());
    }

    @Override
    protected ResourceList<InitialSampling> getFirstList() {
        return getProject().getInitialSamplings();
    }

    @Override
    protected ResourceList<OrthogonalSpace> getSecondList() {
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
        // TODO
        //user has to be aware that this generally invalidates the experiment.
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void valuesChanged(RobustnessSettingsValues values) {
        changeValues(values);
    }
}