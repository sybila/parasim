package org.sybila.parasim.extension.projectManager.view.frame;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import org.sybila.parasim.extension.projectManager.api.ExperimentListener;
import org.sybila.parasim.extension.projectManager.api.ProjectManager;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ProjectManagerWindow extends JFrame implements ProjectManager {

    private ExperimentListener launcher;
    private Action newAction, loadAction, saveAction, launchAction, showAction, quitAction;

    public ProjectManagerWindow() {
        newAction = IconSource.getNewAction(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        loadAction = IconSource.getLoadAction(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        saveAction = IconSource.getSaveAction(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                throw new UnsupportedOperationException("Not supported yet.");
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
                // checks whether saved //

                dispose();
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
