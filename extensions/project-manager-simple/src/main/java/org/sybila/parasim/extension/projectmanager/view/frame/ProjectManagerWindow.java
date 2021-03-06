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
package org.sybila.parasim.extension.projectmanager.view.frame;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import org.sybila.parasim.extension.projectmanager.api.Experiment;
import org.sybila.parasim.extension.projectmanager.api.ExperimentListener;
import org.sybila.parasim.extension.projectmanager.api.ProjectManager;
import org.sybila.parasim.extension.projectmanager.model.OdeSystemNames;
import org.sybila.parasim.extension.projectmanager.model.components.ExperimentAvailableListener;
import org.sybila.parasim.extension.projectmanager.model.components.ExperimentModel;
import org.sybila.parasim.extension.projectmanager.model.components.FormulaModel;
import org.sybila.parasim.extension.projectmanager.model.components.RobustnessModel;
import org.sybila.parasim.extension.projectmanager.model.components.SimulationModel;
import org.sybila.parasim.extension.projectmanager.model.project.Project;
import org.sybila.parasim.extension.projectmanager.model.warning.UsedControler;
import org.sybila.parasim.extension.projectmanager.model.warning.UsedWarningModel;
import org.sybila.parasim.extension.projectmanager.names.ExperimentNames;
import org.sybila.parasim.extension.projectmanager.project.ResourceException;
import org.sybila.parasim.extension.projectmanager.view.experiment.ExperimentSettings;
import org.sybila.parasim.extension.projectmanager.view.formulae.FormulaeList;
import org.sybila.parasim.extension.projectmanager.view.names.NameManager;
import org.sybila.parasim.extension.projectmanager.view.robustness.RobustnessSettings;
import org.sybila.parasim.extension.projectmanager.view.simulation.SimulationSettings;
import org.sybila.parasim.resources.ParasimIconFactory;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ProjectManagerWindow extends JFrame implements ProjectManager {

    private static GridBagConstraints getConstraints(int x) {
        GridBagConstraints result = new GridBagConstraints();
        result.gridx = x % 2;
        result.gridy = x / 2;
        result.weightx = 1;
        result.weighty = 1;
        result.fill = GridBagConstraints.BOTH;
        return result;
    }
    private ExperimentListener launcher;
    private Project project = null;
    //
    private ProjectLoader projectCreator, projectLoader;
    private final Action newAction, loadAction, saveAction, launchAction, showAction, quitAction;
//    private JToggleButton useOctaveSimulationEngineToggle;
    private JPanel projectPanel = null;
    private ExperimentModel experimentModel;
    private final ExperimentAvailableListener experimentModelListener;

    public ProjectManagerWindow() {
        projectCreator = new ProjectImporter();
        projectLoader = new ProjectOpener();

//        useOctaveSimulationEngineToggle = new JToggleButton("Use Octave Simulation Engine",true);
//        useOctaveSimulationEngineToggle.addItemListener(new ItemListener() {
//            public void itemStateChanged(ItemEvent ev) {
//                if(ev.getStateChange()==ItemEvent.SELECTED){
//
//                } else if(ev.getStateChange()==ItemEvent.DESELECTED){
//
//                }
//            }
//        });

        newAction = IconSource.getNewAction(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (checkSaved()) {
                    setActionsEnabled(false);
                    project = projectCreator.loadProject();
                    if (project != null) {
                        buildProjectWindow();
                    }
                    setActionsEnabled(true);
                }
            }
        });
        loadAction = IconSource.getLoadAction(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (checkSaved()) {
                    setActionsEnabled(false);
                    project = projectLoader.loadProject();
                    if (project != null) {
                        buildProjectWindow();
                    }
                    setActionsEnabled(true);
                }
            }
        });
        saveAction = IconSource.getSaveAction(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    setActionsEnabled(false);
                    project.save();
                    setActionsEnabled(true);
                } catch (ResourceException re) {
                    JOptionPane.showMessageDialog(ProjectManagerWindow.this, "Unable to save project: " + re.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        launchAction = new AbstractAction("Launch Experiment") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (launcher != null && checkDangerousAction()) {
                    Experiment experiment = experimentModel.getExperiment();
                    launcher.performExperiment(experiment);
                }
            }
        };
        showAction = new AbstractAction("Show Last Results") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (launcher != null && checkDangerousAction()) {
                    launcher.showResult(experimentModel.getExperiment());
                }
            }
        };
        quitAction = new AbstractAction("Quit") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (checkSaved()) {
                    dispose();
                }
            }
        };

        saveAction.setEnabled(false);
        launchAction.setEnabled(false);
        showAction.setEnabled(false);

        experimentModelListener = new ExperimentAvailableListener() {

            @Override
            public void experimentReady(boolean results) {
                launchAction.setEnabled(true);
                showAction.setEnabled(results);
            }

            @Override
            public void invalidate() {
                launchAction.setEnabled(false);
                showAction.setEnabled(false);
            }
        };

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                quitAction.actionPerformed(new ActionEvent(e.getSource(), e.getID(), "quit"));
            }
        });
        setLayout(new BorderLayout());

        generateToolBar();
        generateMenu();
        setTitle();
        pack();
        setIconImage(ParasimIconFactory.getInstance().getIcon());
    }

    private void generateToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.add(newAction);
        toolBar.add(loadAction);
        toolBar.add(saveAction);
        toolBar.addSeparator();
        toolBar.add(launchAction);
        toolBar.add(showAction);
//        toolBar.addSeparator();
//        toolBar.add(useOctaveSimulationEngineToggle);
        add(toolBar, BorderLayout.PAGE_START);
    }

    private void generateMenu() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // Project //
        JMenu menu = new JMenu("Project");
        menu.setMnemonic(KeyEvent.VK_P);
        menuBar.add(menu);
        // NEW project //
        JMenuItem item = new JMenuItem(newAction);
        item.setText("New");
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        menu.add(item);
        // LOAD project //
        item = new JMenuItem(loadAction);
        item.setText("Open");
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        menu.add(item);
        // SAVE project //
        item = new JMenuItem(saveAction);
        item.setText("Save");
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        menu.add(item);
        // separator //
        menu.addSeparator();
        // QUIT //
        item = new JMenuItem(quitAction);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));
        menu.add(item);

        // Experiment //
        menu = new JMenu("Experiment");
        menu.setMnemonic(KeyEvent.VK_E);
        menuBar.add(menu);
        // LAUNCH experiment //
        item = new JMenuItem(launchAction);
        item.setText("Launch");
        menu.add(item);
        // SHOW results //
        item = new JMenuItem(showAction);
        menu.add(item);

    }

    private boolean isSaved() {
        if (project == null) {
            return true;
        }
        return project.isSaved();
    }

    private boolean checkDangerousAction() {
        return checkSaved("It is recommended to save project before the following action, as it may cause data loss. Do you wish to save this project?");
    }

    private boolean checkSaved() {
        return checkSaved("This project is not saved. If you continue with this operation, some data may be lost. Do you wish to save this project?");
    }

    private boolean checkSaved(String message) {
        if (isSaved()) {
            return true;
        }
        int choice = JOptionPane.showConfirmDialog(this, message, "Unsaved Project", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        switch (choice) {
            case JOptionPane.YES_OPTION:
                saveAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "saveclose"));
                if (!isSaved()) {
                    return false;
                }
            case JOptionPane.NO_OPTION:
                return true;
            case JOptionPane.CANCEL_OPTION:
            default:
                return false;
        }
    }

    private void buildProjectWindow() {
        saveAction.setEnabled(false);
        showAction.setEnabled(false);

        OdeSystemNames names = new OdeSystemNames(project.getOdeSystem());
        Set<String> simulationsNames = new HashSet<>(project.getSimulationSpaces().getNames());
        simulationsNames.retainAll(project.getPrecisionsConfigurations().getNames());
        Set<String> robustnessNames = new HashSet<>(project.getInitialSpaces().getNames());

        experimentModel = new ExperimentModel(project, experimentModelListener);
        FormulaModel formulaModel = new FormulaModel(project, experimentModel);
        SimulationModel simulationModel = new SimulationModel(project, experimentModel);
        RobustnessModel robustnessModel = new RobustnessModel(project, experimentModel);

        JPanel experimentPanel = setUpComponentPanel("Experiment");
        ExperimentSettings experiments = new ExperimentSettings(experimentModel, project.getFormulae().getNames(), simulationsNames, robustnessNames);
        experimentPanel.add(experiments, BorderLayout.CENTER);
        NameManager experimentsManager = new NameManager(experimentModel, project.getExperiments().getNames());
        experimentPanel.add(experimentsManager, BorderLayout.PAGE_END);
        experimentModel.registerExperimentSettings(experiments);

        FormulaeList formulae = new FormulaeList(formulaModel, experiments.getFormulaeNameList());
        addBorder(formulae, "Properties ");
        experimentModel.registerFormulaeManager(formulae);

        JPanel simulationPanel = setUpComponentPanel("Simulation");
        SimulationSettings simulations = new SimulationSettings(simulationModel, names);
        simulationPanel.add(simulations, BorderLayout.CENTER);
        NameManager simulationsManager = new NameManager(simulationModel, experiments.getSimulationsNameList());
        simulationPanel.add(simulationsManager, BorderLayout.PAGE_END);
        experimentModel.registerSimulationsManager(simulationsManager);
        simulationModel.registerSettings(simulations);

        UsedWarningModel simulationWarning = new UsedWarningModel(new WarningLabel(simulationPanel), new UsedControler() {

            @Override
            public boolean isUsed(String name) {
                return project.getSimulationSpaces().isUsedInExperiment(name);
            }
        });
        experimentModel.registerSimulationsWarning(simulationWarning);
        simulationModel.registerWarningLabel(simulationWarning);


        JPanel robustnessPanel = setUpComponentPanel("Perturbation");
        RobustnessSettings robustness = new RobustnessSettings(robustnessModel, names);
        robustnessPanel.add(robustness, BorderLayout.CENTER);
        NameManager robustnessManager = new NameManager(robustnessModel, experiments.getRobustnessNameList());
        robustnessPanel.add(robustnessManager, BorderLayout.PAGE_END);
        experimentModel.registerRobustnessManager(robustnessManager);
        robustnessModel.registerSettings(robustness);

        UsedWarningModel robustnessWarning = new UsedWarningModel(new WarningLabel(robustnessPanel), new UsedControler() {

            @Override
            public boolean isUsed(String name) {
                return project.getInitialSpaces().isUsedInExperiment(name);
            }
        });
        experimentModel.regiseterRobustnessWarning(robustnessWarning);
        robustnessModel.registerWarningLabel(robustnessWarning);

        String experimentName = null;
        String simulationsName = null;
        String robustnessName = null;
        if (!project.getExperiments().getNames().isEmpty()) {
            experimentName = project.getExperiments().getNames().iterator().next();
            ExperimentNames experiment = project.getExperiments().get(experimentName);
            simulationsName = experiment.getSimulationSpaceName();
            robustnessName = experiment.getInitialSpaceName();
        }
        if (simulationsName == null && !project.getSimulationSpaces().getNames().isEmpty()) {
            simulationsName = project.getSimulationSpaces().getNames().iterator().next();
        }
        if (robustnessName == null && !project.getInitialSpaces().getNames().isEmpty()) {
            robustnessName = project.getInitialSpaces().getNames().iterator().next();
        }

        if (experimentName != null) {
            experimentsManager.setSelectedName(experimentName);
            experimentModel.selectionChanged(experimentName);
        }
        if (simulationsName != null) {
            simulationsManager.setSelectedName(simulationsName);
            simulationModel.selectionChanged(simulationsName);
        }
        if (robustnessName != null) {
            robustnessManager.setSelectedName(robustnessName);
            robustnessModel.selectionChanged(robustnessName);
        }

        setUpProjectPanel();
        projectPanel.add(experimentPanel, getConstraints(0));
        projectPanel.add(formulae, getConstraints(1));
        projectPanel.add(simulationPanel, getConstraints(2));
        projectPanel.add(robustnessPanel, getConstraints(3));
        pack();
        setTitle();

        // selfcheck //
        if (!experimentModel.isReady()) {
            throw new IllegalStateException("Experiment model not ready.");
        }
        if (!simulationModel.isReady()) {
            throw new IllegalStateException("Simulation model not ready.");
        }
        if (!robustnessModel.isReady()) {
            throw new IllegalStateException("Robustness model not ready.");
        }
    }

    private void addBorder(JComponent target, String title) {
        target.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), title));
    }

    private void setUpProjectPanel() {
        if (projectPanel != null) {
            remove(projectPanel);
        }
        projectPanel = new JPanel(new GridBagLayout());
        add(projectPanel, BorderLayout.CENTER);
    }

    private JPanel setUpComponentPanel(String title) {
        JPanel result = new JPanel(new BorderLayout());
        addBorder(result, title);
        return result;
    }

    private void setActionsEnabled(boolean enabled) {
        newAction.setEnabled(enabled);
        loadAction.setEnabled(enabled);
        saveAction.setEnabled(enabled);
        if (project == null) {
            saveAction.setEnabled(false);
        }
    }

    private void setTitle() {
        StringBuilder title = new StringBuilder("PARASIM project manager");
        if (project != null) {
            title.append(": ");
            title.append(project.getProjectName());
        }
        setTitle(title.toString());
    }

    @Override
    public void setExperimentListener(ExperimentListener target) {
        launcher = target;
    }

    @Override
    public ExperimentListener getExperimentListener() {
        return launcher;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                ProjectManagerWindow manager = new ProjectManagerWindow();
                manager.setVisible(true);
                manager.addWindowListener(new WindowAdapter() {

                    @Override
                    public void windowClosed(WindowEvent e) {
                        System.exit(0);
                    }
                });
                manager.setExperimentListener(new ExperimentListener() {

                    @Override
                    public void performExperiment(Experiment target) {
                        JOptionPane.showMessageDialog(null, "Experiment performed.");
                    }

                    @Override
                    public void showResult(Experiment target) {
                        JOptionPane.showMessageDialog(null, "Results showed.");
                    }
                });
            }
        });
    }
}
