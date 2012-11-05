package org.sybila.parasim.extension.projectManager.model.components;

import javax.swing.JOptionPane;
import org.sybila.parasim.extension.projectManager.model.project.Project;
import org.sybila.parasim.extension.projectManager.names.ExperimentNames;
import org.sybila.parasim.extension.projectManager.view.experiment.ExperimentSettingsModel;
import org.sybila.parasim.extension.projectManager.view.experiment.ExperimentSettingsValues;
import org.sybila.parasim.extension.projectManager.view.names.NameChooserModel;
import org.sybila.parasim.extension.projectManager.view.names.NameManagerModel;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ExperimentModel implements ExperimentSettingsModel, NameManagerModel {

    private final NameChooserModel formulaeChooser, robustnessChooser, simulationChooser;
    private final Project project;
    private String currentName = null;
    private ExperimentNames current = new ExperimentNames();

    private void checkCurrentName() {
        if (currentName == null) {
            throw new IllegalStateException("Current name is not chosen.");
        }
    }

    private void checkName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Argument (name) is null.");
        }
    }

    public ExperimentModel(Project targetProject) {
        if (targetProject == null) {
            throw new IllegalArgumentException("Argument (project) is null.");
        }
        project = targetProject;

        formulaeChooser = new NameChooserModel() {

            @Override
            public void chooseName(String name) {
                // user SHOULD know this invalidates experiment
                checkName(name);
                current.setFormulaName(name);
            }

            @Override
            public void seeName(String name) {
                /*
                 * pošle jiné komponentě vzkaz -- ta zareaguje -- pokud je nové
                 * jméno, zeptá se na uložení
                 */
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        robustnessChooser = new NameChooserModel() {

            @Override
            public void chooseName(String name) {
                // user SHOULD know this invalidates experiment
                checkName(name);
                current.setInitialSpaceName(name);
                current.setInitialSamplingName(name);
            }

            @Override
            public void seeName(String name) {
                /*
                 * pošle jiné komponentě vzkaz -- ta zareaguje -- pokud je nové
                 * jméno, zeptá se na uložení
                 */
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        simulationChooser = new NameChooserModel() {

            @Override
            public void chooseName(String name) {
                // user SHOULD know this invalidates experiment
                checkName(name);
                checkCurrentName();
                current.setPrecisionConfigurationName(name);
                current.setSimulationSpaceName(name);
            }

            @Override
            public void seeName(String name) {
                /*
                 * pošle jiné komponentě vzkaz -- ta zareaguje -- pokud je nové
                 * jméno, zeptá se na uložení
                 */
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @Override
    public void newName() {
        currentName = null;
        current = new ExperimentNames();
    }

    @Override
    public boolean removeCurrent() {
        checkCurrentName();
        project.getExperiments().remove(currentName);
        current = new ExperimentNames();
        currentName = null;
        return true;
    }

    @Override
    public boolean renameCurrent(String newName) {
        checkName(newName);
        checkCurrentName();
        if (project.getExperiments().rename(currentName, newName)) {
            currentName = newName;
            return true;
        }
        JOptionPane.showMessageDialog(null, "Unable to rename experiment `" + currentName + "' to `" + newName + "'.", "Rename Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    @Override
    public boolean saveCurrent(String name) {
        checkName(name);
        if (project.getExperiments().add(currentName, current)) {
            currentName = name;
            return true;
        }
        JOptionPane.showMessageDialog(null, "Unable to save current experiment as `" + name + "'.", "Save Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    @Override
    public void selectionChanged(String name) {
        checkName(name);
        ExperimentNames values = project.getExperiments().get(name);
        if (values == null) {
            throw new IllegalArgumentException("No experiment of name `" + name + "'.");
        }
        current = values;
        currentName = name;
    }

    @Override
    public void valuesChanged(ExperimentSettingsValues values) {
        current.setIterationLimit(values.getIterationLimit());
        current.setTimeout(values.getTimeout());
    }

    @Override
    public NameChooserModel getFormulaChooser() {
        return formulaeChooser;
    }

    @Override
    public NameChooserModel getRobustnessChooser() {
        return robustnessChooser;
    }

    @Override
    public NameChooserModel getSimulationChooser() {
        return simulationChooser;
    }
}
