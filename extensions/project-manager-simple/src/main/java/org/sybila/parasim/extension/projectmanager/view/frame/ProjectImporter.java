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
package org.sybila.parasim.extension.projectmanager.view.frame;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.sybila.parasim.extension.projectmanager.model.project.Project;
import org.sybila.parasim.extension.projectmanager.model.projectimpl.DirProject;
import org.sybila.parasim.extension.projectmanager.names.ExperimentSuffixes;
import org.sybila.parasim.extension.projectmanager.project.ResourceException;
import org.sybila.parasim.extension.projectmanager.view.ListeningFileChooser;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.sbml.SBMLOdeSystemFactory;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ProjectImporter implements ProjectLoader {

    private static final String ERROR_HEADER = "Project Creation Error";
    private final JDialog importer;
    private final ListeningFileChooser modelChooser, projectChooser;
    private final JButton approveBtn;
    private final JLabel info;
    private boolean approved;

    private String removeExtension(String name) {
        if (name.endsWith(".sbml")) {
            return name.substring(0, name.length() - 5);
        } else if (name.endsWith(".xml")) {
            return name.substring(0, name.length() - 4);
        } else {
            return name;
        }
    }

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
        modelChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        modelChooser.setAcceptAllFileFilterUsed(false);
        modelChooser.addChoosableFileFilter(new FileNameExtensionFilter("SBML files", "sbml"));
        modelChooser.addChoosableFileFilter(new FileNameExtensionFilter("XML files", "xml"));
        modelChooser.addSelectedFileChangedListener(changeListener);
        modelChooser.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Select Model File"));
        importer.add(modelChooser);


        JPanel projectPanel = new JPanel();
        projectPanel.setLayout(new BoxLayout(projectPanel, BoxLayout.PAGE_AXIS));
        projectPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Select Project Directory"));
        importer.add(projectPanel);

        projectChooser = new ListeningFileChooser();
        projectChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        projectChooser.addSelectedFileChangedListener(changeListener);
        projectPanel.add(projectChooser);

        info = new JLabel("Project directory is not empty.");
        info.setAlignmentX(0.5f);
        info.setForeground(Color.RED);
        projectPanel.add(info);

        JPanel buttons = new JPanel();
        approveBtn = new JButton(new AbstractAction("OK") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                approved = true;
                importer.setVisible(false);
            }
        });
        buttons.add(approveBtn);
        buttons.add(new JButton(new AbstractAction("Cancel") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                importer.setVisible(false);
            }
        }));
        importer.add(buttons);
        importer.pack();

        checkSelection();
    }

    private void checkSelection() {
        boolean correct = ((modelChooser.getSelectedFile() != null) && modelChooser.getSelectedFile().isFile());
        info.setVisible(false);
        if (projectChooser.getSelectedFile() != null && projectChooser.getSelectedFile().canRead()) {
            if (projectChooser.getSelectedFile().list().length != 0) {
                info.setVisible(true);
                correct = false;
            }
        } else {
            correct = false;
        }
        approveBtn.setEnabled(correct);
    }

    private void showImportError(String message) {
        JOptionPane.showMessageDialog(null, message, ERROR_HEADER, JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public Project loadProject() {
        approved = false;
        importer.setVisible(true);

        //gets back when the window is hidden
        if (!approved) {
            return null;
        }

        try {
            OdeSystem test = SBMLOdeSystemFactory.fromFile(modelChooser.getSelectedFile());
        } catch (IOException ioe) {
            showImportError("Unable to load model (it probably is not SBML file):\n" + ioe.getMessage());
            return null;
        }


        String name = ExperimentSuffixes.MODEL.add(removeExtension(modelChooser.getSelectedFile().getName()));
        File target = new File(projectChooser.getSelectedFile(), name);
        FileChannel src = null;
        FileChannel dst = null;
        try {
            src = new FileInputStream(modelChooser.getSelectedFile()).getChannel();
            dst = new FileOutputStream(target).getChannel();
            dst.transferFrom(src, 0, src.size());
        } catch (FileNotFoundException fnfe) {
            showImportError("Unable to create project:\n" + fnfe.getMessage());
            return null;
        } catch (IOException ioe) {
            showImportError("Unable to create project:\n" + ioe.getMessage());
            return null;
        } finally {
            if (src != null) {
                try {
                    src.close();
                } catch (IOException ioe) {
                    showImportError("Unable to create project:\n" + ioe.getMessage());
                    return null;
                }
            }
            if (dst != null) {
                try {
                    dst.close();
                } catch (IOException ioe) {
                    showImportError("Unable to create project:\n" + ioe.getMessage());
                    return null;
                }
            }
        }
        try {
            return new DirProject(projectChooser.getSelectedFile());
        } catch (ResourceException re) {
            showImportError("Unable to create project:\n" + re.getMessage());
            return null;
        }
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
