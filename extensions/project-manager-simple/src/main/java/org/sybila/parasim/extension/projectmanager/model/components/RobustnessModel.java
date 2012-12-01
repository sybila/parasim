/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
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