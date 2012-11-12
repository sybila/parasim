package org.sybila.parasim.extension.projectmanager.view.frame;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.sybila.parasim.extension.projectmanager.model.project.Project;
import org.sybila.parasim.extension.projectmanager.view.ListeningFileChooser;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ProjectImporter implements ProjectLoader {

    private final JDialog importer;
    private final ListeningFileChooser modelChooser, projectChooser;
    private final JButton approveBtn;
    private boolean approved;

    public ProjectImporter() {
        importer = new JDialog();
        importer.setTitle("Create New Project");
        importer.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        importer.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        importer.getContentPane().setLayout(new BoxLayout(importer.getContentPane(), BoxLayout.PAGE_AXIS));

        ActionListener changeListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                checkSelection();
            }
        };

        modelChooser = new ListeningFileChooser();
        modelChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        modelChooser.setAcceptAllFileFilterUsed(false);
        modelChooser.addChoosableFileFilter(new FileNameExtensionFilter("SBML files", "sbml", "xml"));
        modelChooser.addSelectedFileChangedListener(changeListener);
        modelChooser.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Select Model File"));
        importer.add(modelChooser);

        projectChooser = new ListeningFileChooser();
        projectChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        projectChooser.addSelectedFileChangedListener(changeListener);
        projectChooser.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Select Project Directory"));
        importer.add(projectChooser);

        JPanel buttons = new JPanel();
        approveBtn = new JButton(new AbstractAction("OK") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                approved = true;
                importer.setVisible(false);
            }
        });
        buttons.add(approveBtn);
        approveBtn.setEnabled(false);
        buttons.add(new JButton(new AbstractAction("Cancel") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                importer.setVisible(false);
            }
        }));
        importer.add(buttons);
        importer.pack();
    }

    private void checkSelection() {
        approveBtn.setEnabled((modelChooser.getSelectedFile() != null) && (projectChooser.getSelectedFile() != null));
    }

    @Override
    public Project loadProject() {
        approved = false;
        importer.setVisible(true);

        //gets back when the window is hidden
        if (!approved) {
            return null;
        }

        //tady se zkopíruje soubor a otevře se projekt -- měla by tam možná být nějaká kontrola pro případ, že directory není prázdný

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
