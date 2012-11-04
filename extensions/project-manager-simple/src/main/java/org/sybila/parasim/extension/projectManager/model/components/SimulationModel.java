package org.sybila.parasim.extension.projectManager.model.components;

import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.extension.projectManager.model.OrthogonalSpaceFactory;
import org.sybila.parasim.extension.projectManager.model.project.Project;
import org.sybila.parasim.extension.projectManager.model.project.ResourceList;
import org.sybila.parasim.extension.projectManager.view.names.NameManagerModel;
import org.sybila.parasim.extension.projectManager.view.simulation.SimulationSettingsModel;
import org.sybila.parasim.extension.projectManager.view.simulation.SimulationSettingsValues;
import org.sybila.parasim.model.space.OrthogonalSpace;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class SimulationModel extends DoubleListNameManagerModel<PrecisionConfiguration, OrthogonalSpace, SimulationSettingsValues> implements SimulationSettingsModel, NameManagerModel {

    private final OrthogonalSpaceFactory spaceFactory;

    public SimulationModel(Project targetProject) {
        super(targetProject);
        spaceFactory = new OrthogonalSpaceFactory(targetProject.getOdeSystem());
    }

    @Override
    protected ResourceList<PrecisionConfiguration> getFirstList() {
        return getProject().getPrecisionsConfigurations();
    }

    @Override
    protected ResourceList<OrthogonalSpace> getSecondList() {
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
        //TODO
        //user has to be aware that this generally invalidates the experiment.
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void valuesChanged(SimulationSettingsValues values) {
        changeValues(values);
    }
}
