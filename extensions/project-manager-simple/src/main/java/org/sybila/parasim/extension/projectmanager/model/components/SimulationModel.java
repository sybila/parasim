package org.sybila.parasim.extension.projectmanager.model.components;

import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.extension.projectmanager.model.OrthogonalSpaceFactory;
import org.sybila.parasim.extension.projectmanager.model.project.ExperimentResourceList;
import org.sybila.parasim.extension.projectmanager.model.project.Project;
import org.sybila.parasim.extension.projectmanager.view.names.NameManagerModel;
import org.sybila.parasim.extension.projectmanager.view.simulation.SimulationSettingsModel;
import org.sybila.parasim.extension.projectmanager.view.simulation.SimulationSettingsValues;
import org.sybila.parasim.model.space.OrthogonalSpace;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class SimulationModel extends DoubleListNameManagerModel<PrecisionConfiguration, OrthogonalSpace, SimulationSettingsValues> implements SimulationSettingsModel, NameManagerModel {

    private final OrthogonalSpaceFactory spaceFactory;
    private final ExperimentModel experiment;

    public SimulationModel(Project targetProject, ExperimentModel experimentModel) {
        super(targetProject);
        if (experimentModel == null) {
            throw new IllegalArgumentException("Argument (experiment model) is null.");
        }
        spaceFactory = new OrthogonalSpaceFactory(targetProject.getOdeSystem());
        experiment = experimentModel;
    }

    @Override
    protected ExperimentResourceList<PrecisionConfiguration> getFirstList() {
        return getProject().getPrecisionsConfigurations();
    }

    @Override
    protected ExperimentResourceList<OrthogonalSpace> getSecondList() {
        return getProject().getSimulationSpaces();
    }

    @Override
    protected SimulationSettingsValues getValue(PrecisionConfiguration first, OrthogonalSpace second) {
        return new SimulationSettingsValues(first, spaceFactory.get(second), second.getMinBounds().getTime(), second.getMaxBounds().getTime());
    }

    @Override
    protected PrecisionConfiguration getFirstValue(SimulationSettingsValues value) {
        return value.getPrecisionConfiguration();
    }

    @Override
    protected OrthogonalSpace getSecondValue(SimulationSettingsValues value) {
        return spaceFactory.get(value.getSimulationSpace(), value.getSimulationStart(), value.getSimulationEnd());
    }

    @Override
    public void chooseCurrent() {
        checkCurrentName();
        experiment.getSimulationChooser().chooseName(getCurrentName());
    }

    @Override
    public void valuesChanged(SimulationSettingsValues values) {
        changeValues(values);
    }
}
