package org.sybila.parasim.extension.projectmanager.model.components;

import javax.swing.JOptionPane;
import org.sybila.parasim.application.model.LoadedExperiment;
import org.sybila.parasim.extension.projectmanager.model.project.Project;
import org.sybila.parasim.extension.projectmanager.names.ExperimentNames;
import org.sybila.parasim.extension.projectmanager.view.experiment.ExperimentSettings;
import org.sybila.parasim.extension.projectmanager.view.experiment.ExperimentSettingsModel;
import org.sybila.parasim.extension.projectmanager.view.experiment.ExperimentSettingsValues;
import org.sybila.parasim.extension.projectmanager.view.formulae.FormulaeList;
import org.sybila.parasim.extension.projectmanager.view.names.NameChooserModel;
import org.sybila.parasim.extension.projectmanager.view.names.NameManager;
import org.sybila.parasim.extension.projectmanager.view.names.NameManagerModel;
import org.sybila.parasim.util.Pair;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ExperimentModel implements ExperimentSettingsModel, NameManagerModel {

    private final NameChooserModel formulaeChooser, robustnessChooser, simulationChooser;
    private final Project project;
    private final ExperimentAvailableListener experimentListener;
    private String currentName = null;
    private ExperimentNames current = new ExperimentNames();
    //
    private NameManager robustness, simulations;
    private FormulaeList formulae;
    private ExperimentSettings settings = null;

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

    private void checkExperiment() {
        if (current != null && current.isFilled()) {
            experimentListener.experimentReady();
        } else {
            experimentListener.invalidate();
        }
    }

    public ExperimentModel(Project targetProject, ExperimentAvailableListener listener) {
        if (targetProject == null) {
            throw new IllegalArgumentException("Argument (project) is null.");
        }
        if (listener == null) {
            throw new IllegalArgumentException("Argument (listener) is null.");
        }
        project = targetProject;
        experimentListener = listener;

        formulaeChooser = new NameChooserModel() {

            @Override
            public void chooseName(String name) {
                // user SHOULD know this invalidates experiment
                checkName(name);
                current.setFormulaName(name);
                checkExperiment();
            }

            @Override
            public void seeName(String name) {
                formulae.setSelectedName(name);
            }
        };
        robustnessChooser = new NameChooserModel() {

            @Override
            public void chooseName(String name) {
                // user SHOULD know this invalidates experiment
                checkName(name);
                current.setInitialSpaceName(name);
                current.setInitialSamplingName(name);
                checkExperiment();
            }

            @Override
            public void seeName(String name) {
                robustness.chooseName(name);
            }
        };
        simulationChooser = new NameChooserModel() {

            @Override
            public void chooseName(String name) {
                // user SHOULD know this invalidates experiment
                checkName(name);
                current.setPrecisionConfigurationName(name);
                current.setSimulationSpaceName(name);
                checkExperiment();
            }

            @Override
            public void seeName(String name) {
                simulations.chooseName(name);
            }
        };

        formulae = null;
        robustness = null;
        simulations = null;
    }

    public void registerFormulaeManager(FormulaeList manager) {
        formulae = manager;
    }

    public void registerRobustnessManager(NameManager manager) {
        robustness = manager;
    }

    public void registerSimulationsManager(NameManager manager) {
        simulations = manager;
    }

    public void registerExperimentSettings(ExperimentSettings settings) {
        this.settings = settings;
    }

    public boolean isReady() {
        return (formulae != null) && (robustness != null) && (simulations != null) && (settings != null);
    }

    public LoadedExperiment getExperiment() {
        if (current == null) {
            return null;
        }
        if (!current.isFilled()) {
            return null;
        }
        return project.getExperiment(current);
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
            current.setVerificationResultName(currentName);
            return true;
        }
        JOptionPane.showMessageDialog(null, "Unable to rename experiment `" + currentName + "' to `" + newName + "'.", "Rename Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    @Override
    public boolean saveCurrent(String name) {
        checkName(name);
        if (project.getExperiments().add(name, current)) {
            selectionChanged(name);
            current.setVerificationResultName(currentName);
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

        settings.setValues(new Pair<>(new ExperimentSettingsValues(values.getIterationLimit(), values.getTimeout()), values.getAnnotation()));
        settings.getFormulaeNameList().selectName(values.getFormulaName());
        settings.getSimulationsNameList().selectName(values.getSimulationSpaceName());
        settings.getRobustnessNameList().selectName(values.getInitialSamplingName());
        checkExperiment();
    }

    @Override
    public void valuesChanged(ExperimentSettingsValues values) {
        current.setIterationLimit(values.getIterationLimit());
        current.setTimeout(values.getTimeout());
        checkExperiment();
    }

    @Override
    public void annotationChanged(String annotation) {
        current.setAnnotation(annotation);
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
