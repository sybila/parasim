package org.sybila.parasim.extension.projectmanager.model.components;

import javax.swing.JOptionPane;
import org.sybila.parasim.extension.projectmanager.model.project.ExperimentResourceList;
import org.sybila.parasim.extension.projectmanager.model.project.Project;
import org.sybila.parasim.extension.projectmanager.model.project.ResourceAction;
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

    public DoubleListNameManagerModel(Project targetProject) {
        if (targetProject == null) {
            throw new IllegalArgumentException("Argument (project) is null.");
        }
        this.project = targetProject;
    }

    public void registerSettings(ValueHolder<R> target) {
        settings = target;
    }

    public boolean isReady() {
        return (settings != null);
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
        JOptionPane.showMessageDialog(null, "One or more experiments was invalidated.", "Warning", JOptionPane.WARNING_MESSAGE);
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

    @Override
    public void newName() {
        currentName = null;
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
        currentName = null;
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
            currentName = newName;
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
            currentName = name;
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
        currentName = name;
        settings.setValues(getValue(getFirstList().get(name), getSecondList().get(name)));
    }
}