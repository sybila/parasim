package org.sybila.parasim.extension.projectmanager.view.frame;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import org.sybila.parasim.extension.projectmanager.api.ExperimentListener;
import org.sybila.parasim.extension.projectmanager.api.ProjectManager;
import org.sybila.parasim.extension.projectmanager.model.OdeSystemNames;
import org.sybila.parasim.extension.projectmanager.model.components.ExperimentModel;
import org.sybila.parasim.extension.projectmanager.model.components.FormulaModel;
import org.sybila.parasim.extension.projectmanager.model.components.RobustnessModel;
import org.sybila.parasim.extension.projectmanager.model.components.SimulationModel;
import org.sybila.parasim.extension.projectmanager.model.project.Project;
import org.sybila.parasim.extension.projectmanager.project.ResourceException;
import org.sybila.parasim.extension.projectmanager.view.experiment.ExperimentSettings;
import org.sybila.parasim.extension.projectmanager.view.formulae.FormulaeList;
import org.sybila.parasim.extension.projectmanager.view.names.NameManager;
import org.sybila.parasim.extension.projectmanager.view.robustness.RobustnessSettings;
import org.sybila.parasim.extension.projectmanager.view.simulation.SimulationSettings;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ProjectManagerWindow extends JFrame implements ProjectManager {

    private ExperimentListener launcher;
    private Project project = null;
    //
    private ProjectLoader projectCreator, projectLoader;
    private Action newAction, loadAction, saveAction, launchAction, showAction, quitAction;

    public ProjectManagerWindow() {
        projectCreator = new ProjectImporter();
        projectLoader = new ProjectOpener();

        newAction = IconSource.getNewAction(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (checkSaved()) {
                    project = projectCreator.loadProject();
                    if (project != null) {
                        buildProjectWindow();
                    }
                }
            }
        });
        loadAction = IconSource.getLoadAction(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (checkSaved()) {
                    project = projectLoader.loadProject();
                    if (project != null) {
                        buildProjectWindow();
                    }
                }
            }
        });
        saveAction = IconSource.getSaveAction(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    project.save();
                } catch (ResourceException re) {
                    JOptionPane.showMessageDialog(ProjectManagerWindow.this, "Unable to save project: " + re.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        launchAction = new AbstractAction("Launch Experiment") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        showAction = new AbstractAction("Show Results") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                throw new UnsupportedOperationException("Not supported yet.");
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

        launchAction.setEnabled(false);
        showAction.setEnabled(false);

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
        pack();
    }

    private void generateToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.add(newAction);
        toolBar.add(loadAction);
        toolBar.add(saveAction);
        toolBar.addSeparator();
        toolBar.add(launchAction);
        toolBar.add(showAction);
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

    private boolean checkSaved() {
        if (isSaved()) {
            return true;
        }
        int choice = JOptionPane.showConfirmDialog(this, "This project is not saved. If you continue with this operation,"
                + " some data may be lost. Do you wish to save this project?", "Unsaved Project", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
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
        JPanel projectPanel = setUpProjectPanel();
        OdeSystemNames names = new OdeSystemNames(project.getOdeSystem());
        Set<String> simulationsNames = new HashSet<>(project.getSimulationSpaces().getNames());
        simulationsNames.retainAll(project.getPrecisionsConfigurations().getNames());
        Set<String> robustnessNames = new HashSet<>(project.getInitialSamplings().getNames());
        robustnessNames.retainAll(project.getInitialSpaces().getNames());

        ExperimentModel experimentModel = new ExperimentModel(project);
        FormulaModel formulaModel = new FormulaModel(project, experimentModel);
        SimulationModel simulationModel = new SimulationModel(project, experimentModel);
        RobustnessModel robustnessModel = new RobustnessModel(project, experimentModel);

        JPanel experimentPanel = setUpComponentPanel("Experiment");
        ExperimentSettings experiments = new ExperimentSettings(experimentModel, project.getFormulae().getNames(), simulationsNames, robustnessNames);
        experimentPanel.add(experiments);
        NameManager experimentsManager = new NameManager(experimentModel, project.getExperiments().getNames());
        experimentPanel.add(experimentsManager);

        FormulaeList formulae = new FormulaeList(formulaModel, experiments.getFormulaeNameList());
        addBorder(formulae, "Formula");
        experimentModel.registerFormulaeManager(formulae);

        JPanel simulationPanel = setUpComponentPanel("Simulation");
        SimulationSettings simulations = new SimulationSettings(simulationModel, names);
        simulationPanel.add(simulations);
        NameManager simulationsManager = new NameManager(simulationModel, experiments.getSimulationsNameList());
        simulationPanel.add(simulationsManager);
        experimentModel.registerSimulationsManager(simulationsManager);
        simulationModel.registerSettings(simulations);


        JPanel robustnessPanel = setUpComponentPanel("Robustness");
        RobustnessSettings robustness = new RobustnessSettings(robustnessModel, names);
        robustnessPanel.add(robustness);
        NameManager robustnessManager = new NameManager(robustnessModel, experiments.getRobustnessNameList());
        robustnessPanel.add(robustnessManager);
        experimentModel.registerRobustnessManager(robustnessManager);
        robustnessModel.registerSettings(robustness);

        projectPanel.add(experimentPanel);
        projectPanel.add(formulae);
        projectPanel.add(simulationPanel);
        projectPanel.add(robustnessPanel);
        pack();

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

    private JPanel setUpProjectPanel() {
        JPanel result = new JPanel();
        result.setLayout(new BoxLayout(result, BoxLayout.LINE_AXIS));
        add(result, BorderLayout.CENTER);
        return result;
    }

    private JPanel setUpComponentPanel(String title) {
        JPanel result = new JPanel();
        result.setLayout(new BoxLayout(result, BoxLayout.PAGE_AXIS));
        addBorder(result, title);
        return result;
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
                JFrame manager = new ProjectManagerWindow();
                manager.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                manager.setVisible(true);
            }
        });
    }
}
