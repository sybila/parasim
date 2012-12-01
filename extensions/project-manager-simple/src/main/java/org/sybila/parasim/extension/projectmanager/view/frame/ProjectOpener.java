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
import java.io.FilenameFilter;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.sybila.parasim.extension.projectmanager.model.project.Project;
import org.sybila.parasim.extension.projectmanager.model.projectimpl.DirProject;
import org.sybila.parasim.extension.projectmanager.names.ExperimentSuffixes;
import org.sybila.parasim.extension.projectmanager.project.ResourceException;
import org.sybila.parasim.extension.projectmanager.view.ListeningFileChooser;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ProjectOpener implements ProjectLoader {

    private final JDialog importer;
    private final ListeningFileChooser chooser;
    private final JButton approveBtn;
    private final JLabel info;
    private boolean approved;

    public ProjectOpener() {
        importer = new JDialog();
        importer.setTitle("Open Project");
        importer.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        importer.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        importer.getContentPane().setLayout(new BoxLayout(importer.getContentPane(), BoxLayout.PAGE_AXIS));

        chooser = new ListeningFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.addSelectedFileChangedListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                checkFile(chooser.getSelectedFile());
            }
        });
        importer.add(chooser);

        info = new JLabel("Selected directory does not contain model file (or contains more of them).");
        info.setAlignmentX(0.5f);
        info.setForeground(Color.RED);
        importer.add(info);

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

        setDirectoryOK(false);
        importer.pack();
    }

    private void setDirectoryOK(boolean value) {
        approveBtn.setEnabled(value);
        info.setVisible(!value);
    }

    private void checkFile(File target) {
        if (target != null) {
            String[] models = target.list(new FilenameFilter() {

                @Override
                public boolean accept(File file, String string) {
                    return string.endsWith(ExperimentSuffixes.MODEL.getSuffix());
                }
            });
            setDirectoryOK(models != null && models.length == 1);
        }
    }

    @Override
    public Project loadProject() {
        approved = false;
        importer.setVisible(true);

        //gets back when window is closed
        if (!approved) {
            return null;
        }

        try {
            DirProject result = new DirProject(chooser.getSelectedFile());
            result.loadResources();
            return result;
        } catch (ResourceException re) {
            JOptionPane.showMessageDialog(null, "Unable to load project in `" + chooser.getSelectedFile().toString() + "' directory:\n" + re.getLocalizedMessage(), "Open Project Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public static void main(String[] args) {
        ProjectOpener importer = new ProjectOpener();
        Project result = importer.loadProject();
        if (result == null) {
            JOptionPane.showMessageDialog(null, "No project opened.", "Open Project", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Project `" + result.getProjectName() + "' opened.", "Open Project", JOptionPane.INFORMATION_MESSAGE);
        }
        System.exit(0);
    }
}
