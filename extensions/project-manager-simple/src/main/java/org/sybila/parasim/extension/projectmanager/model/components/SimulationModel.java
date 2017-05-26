/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
