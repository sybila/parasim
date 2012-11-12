package org.sybila.parasim.extension.projectmanager.view.frame;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.sybila.parasim.extension.projectmanager.model.project.Project;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ProjectImporter implements ProjectLoader {

    private final JDialog importer;
    private boolean approved;

    public ProjectImporter() {
        importer = new JDialog();
        importer.setTitle("Create New Project");
        importer.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        importer.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        importer.getContentPane().setLayout(new BoxLayout(importer.getContentPane(), BoxLayout.PAGE_AXIS));


        JPanel buttons = new JPanel();
        buttons.add(new JButton(new AbstractAction("OK") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                approved = true;
                importer.setVisible(false);
            }
        }));
        buttons.add(new JButton(new AbstractAction("Cancel") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                importer.setVisible(false);
            }
        }));
        importer.add(buttons);
        importer.pack();
    }

    @Override
    public Project loadProject() {
        approved = false;
        importer.setVisible(true);

        //gets back when the window is hidden
        if (!approved) {
            return null;
        }


        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static void main(String[] args) {
        ProjectImporter importer = new ProjectImporter();
        Project result = importer.loadProject();
        if (result == null) {
            JOptionPane.showMessageDialog(null, "No project created.", "Project Creation", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Project `" + result.getProjectName() + "' created.", "Project Creation", JOptionPane.INFORMATION_MESSAGE);
        }
        System.exit(0);
    }
}
