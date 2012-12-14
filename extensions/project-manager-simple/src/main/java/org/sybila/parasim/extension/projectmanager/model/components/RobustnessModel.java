/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.extension.projectmanager.model.components;

import javax.swing.JOptionPane;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.extension.projectmanager.model.project.ExperimentResourceList;
import org.sybila.parasim.extension.projectmanager.model.project.Project;
import org.sybila.parasim.extension.projectmanager.model.project.ResourceAction;
import org.sybila.parasim.extension.projectmanager.model.warning.UsedWarningModel;
import org.sybila.parasim.extension.projectmanager.view.names.ExtendedNameManagerModel;
import org.sybila.parasim.extension.projectmanager.view.robustness.RobustnessSettings;
import org.sybila.parasim.extension.projectmanager.view.robustness.RobustnessSettingsModel;
import org.sybila.parasim.extension.projectmanager.view.robustness.RobustnessSettingsValues;
import org.sybila.parasim.extension.projectmanager.view.robustness.RobustnessSettingsValuesFactory;
import org.sybila.parasim.model.space.OrthogonalSpace;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class RobustnessModel implements RobustnessSettingsModel, ExtendedNameManagerModel {

    private final Project project;
    private String currentName = null;
    private final RobustnessSettingsValuesFactory factory;
    private final ExperimentModel experiment;
    private UsedWarningModel warningModel;
    private RobustnessSettings settings;

    public RobustnessModel(Project targetProject, ExperimentModel experimentModel) {
        Validate.notNull(targetProject);
        Validate.notNull(experimentModel);

        project = targetProject;
        factory = new RobustnessSettingsValuesFactory(targetProject.getOdeSystem());
        experiment = experimentModel;
    }

    public void registerWarningLabel(UsedWarningModel model) {
        warningModel = model;
    }

    public void registerSettings(RobustnessSettings settings) {
        this.settings = settings;
    }

    public boolean isReady() {
        return (warningModel != null) && (settings != null);
    }

    private void setCurrentName(String name) {
        currentName = name;
        warningModel.setName(name);
    }

    private void checkCurrentName() {
        if (currentName == null) {
            throw new IllegalStateException("Current name is not chosen.");
        }
    }

    private ExperimentResourceList<OrthogonalSpace> getList() {
        return project.getInitialSpaces();
    }

    @Override
    public void newName() {
        setCurrentName(null);
    }

    @Override
    public boolean removeCurrent() {
        checkCurrentName();
        if (getList().isUsedInExperiment(currentName)
                && (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(null, "This setting is used in one or more experiments."
                + " Do you really want to delete it?", "Delete Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE))) {
            return false;
        }
        getList().remove(currentName);
        setCurrentName(null);
        return true;
    }

    @Override
    public boolean renameCurrent(String newName) {
        Validate.notNull(newName);
        checkCurrentName();

        ResourceAction action = getList().rename(currentName, newName);
        if (action.isViable()) {
            action.commit();
            setCurrentName(newName);
            return true;
        }

        action.revert();
        JOptionPane.showMessageDialog(null, "Unable to rename `" + currentName + "' to `" + newName + "'.", "Rename Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    @Override
    public boolean saveCurrent(String name) {
        Validate.notNull(name);
        RobustnessSettingsValues values = settings.getValues();

        ResourceAction action = getList().add(name, factory.get(values));
        if (action.isViable()) {
            action.commit();
            setCurrentName(name);
            return true;
        }

        action.revert();
        JOptionPane.showMessageDialog(null, "Unable to save `" + name + "'.", "Save Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    @Override
    public void selectionChanged(String name) {
        Validate.notNull(name);
        setCurrentName(name);
        settings.setValues(factory.get(getList().get(name)));
    }

    @Override
    public void chooseCurrent() {
        checkCurrentName();
        experiment.getRobustnessChooser().chooseName(currentName);
    }

    @Override
    public void valuesChanged(RobustnessSettingsValues values) {
        if (currentName != null) {
            getList().put(currentName, factory.get(values));
        }
    }
}