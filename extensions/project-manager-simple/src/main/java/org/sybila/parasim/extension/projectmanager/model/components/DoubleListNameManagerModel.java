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

import javax.swing.JOptionPane;
import org.sybila.parasim.extension.projectmanager.model.project.ExperimentResourceList;
import org.sybila.parasim.extension.projectmanager.model.project.Project;
import org.sybila.parasim.extension.projectmanager.model.project.ResourceAction;
import org.sybila.parasim.extension.projectmanager.model.warning.UsedWarningModel;
import org.sybila.parasim.extension.projectmanager.view.ValueHolder;
import org.sybila.parasim.extension.projectmanager.view.names.ExtendedNameManagerModel;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public abstract class DoubleListNameManagerModel<T, S, R> implements ExtendedNameManagerModel {

    private final Project project;
    private String currentName = null;
    private ValueHolder<R> settings;
    private UsedWarningModel warningModel;

    public DoubleListNameManagerModel(Project targetProject) {
        if (targetProject == null) {
            throw new IllegalArgumentException("Argument (project) is null.");
        }
        this.project = targetProject;
    }

    public void registerSettings(ValueHolder<R> target) {
        settings = target;
    }

    public void registerWarningLabel(UsedWarningModel model) {
        warningModel = model;
    }

    public boolean isReady() {
        return (settings != null) && (warningModel != null);
    }

    protected abstract ExperimentResourceList<T> getFirstList();

    protected abstract ExperimentResourceList<S> getSecondList();

    protected abstract R getValue(T first, S second);

    protected abstract T getFirstValue(R value);

    protected abstract S getSecondValue(R value);

    protected String getCurrentName() {
        return currentName;
    }

    protected Project getProject() {
        return project;
    }

    /**
     * Placeholder method which asks the user whether it is ok to invalidate
     * experiments.
     *
     * @return
     * <code>true</code> when user agrees to invalidate experiments,
     * <code>false</code> otherwise.
     */
    private boolean checkInvalidate() {
        return true;
    }

    protected void checkCurrentName() {
        if (currentName == null) {
            throw new IllegalStateException("Current name is not chosen.");
        }
    }

    private void checkName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Argument (name) is null.");
        }
    }

    protected void changeValues(R values) {
        if (currentName != null) {
            if ((getFirstList().isUsedInExperiment(currentName) || getSecondList().isUsedInExperiment(currentName)) && !checkInvalidate()) {
                settings.setValues(getValue(getFirstList().get(currentName), getSecondList().get(currentName)));
                return;
            }
            getFirstList().put(currentName, getFirstValue(values));
            getSecondList().put(currentName, getSecondValue(values));
        }
    }

    private void setCurrentName(String name) {
        currentName = name;
        warningModel.setName(name);
    }

    @Override
    public void newName() {
        setCurrentName(null);
    }

    @Override
    public boolean removeCurrent() {
        checkCurrentName();
        if ((getFirstList().isUsedInExperiment(currentName) || getSecondList().isUsedInExperiment(currentName))
                && (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(null, "This setting is used in one or more experiments."
                + " Do you really want to delete it?", "Delete Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE))) {
            return false;
        }

        getFirstList().remove(currentName);
        getSecondList().remove(currentName);
        setCurrentName(null);
        return true;
    }

    @Override
    public boolean renameCurrent(String newName) {
        checkName(newName);
        checkCurrentName();

        ResourceAction first = getFirstList().rename(currentName, newName);
        ResourceAction second = getSecondList().rename(currentName, newName);

        if (first.isViable() && second.isViable()) {
            first.commit();
            second.commit();
            setCurrentName(newName);
            return true;
        }

        first.revert();
        second.revert();
        JOptionPane.showMessageDialog(null, "Unable to rename `" + currentName + "' to `" + newName + "'.", "Rename Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    @Override
    public boolean saveCurrent(String name) {
        checkName(name);
        R values = settings.getValues();

        ResourceAction first = getFirstList().add(name, getFirstValue(values));
        ResourceAction second = getSecondList().add(name, getSecondValue(values));
        if (first.isViable() && second.isViable()) {
            first.commit();
            second.commit();
            setCurrentName(name);
            return true;
        }

        first.revert();
        second.revert();
        JOptionPane.showMessageDialog(null, "Unable to save `" + name + "'.", "Save Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    @Override
    public void selectionChanged(String name) {
        checkName(name);
        setCurrentName(name);
        settings.setValues(getValue(getFirstList().get(name), getSecondList().get(name)));
    }
}