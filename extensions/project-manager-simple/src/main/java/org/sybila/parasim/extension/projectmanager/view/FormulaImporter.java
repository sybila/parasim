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
package org.sybila.parasim.extension.projectmanager.view;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.sybila.parasim.util.Pair;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class FormulaImporter {

    private boolean approved;
    private JDialog importer;
    private ListeningFileChooser chooser;
    private JTextField nameField;
    private final JButton approveBtn;

    private String removeExtension(File target) {
        String name = target.getName();
        if (name.endsWith(".xml") || name.endsWith(".stl")) {
            return name.substring(0, name.length() - 4);
        }
        return name;
    }

    public FormulaImporter() {
        importer = new JDialog();
        importer.setTitle("Import Formula");
        importer.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        importer.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        importer.getContentPane().setLayout(new BoxLayout(importer.getContentPane(), BoxLayout.PAGE_AXIS));

        chooser = new ListeningFileChooser();
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("XML files", "xml"));
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("STL formulae", "stl"));

        chooser.addSelectedFileChangedListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                boolean chosen = (chooser.getSelectedFile() != null) && (chooser.getSelectedFile().isFile());
                nameField.setText(chosen ? removeExtension(chooser.getSelectedFile()) : null);
                approveBtn.setEnabled(chosen);
            }
        });

        chooser.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Formula File"));
        importer.getContentPane().add(chooser);

        nameField = new JTextField();
        nameField.setBorder(null);
        nameField.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Formula Name"));
        importer.getContentPane().add(nameField);

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

    public Pair<File, String> showDialog() {
        approved = false;
        importer.setVisible(true);

        //gets back when the window is hidden
        if (!approved) {
            return null;
        }

        File target = chooser.getSelectedFile();
        if (target == null) {
            JOptionPane.showMessageDialog(null, "No formula file chosen.", "Import Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        String name = nameField.getText();
        if (name == null || name.isEmpty()) {
            name = removeExtension(target);
        }

        return new Pair<>(target, name);
    }

    public static void main(String[] args) {
        FormulaImporter importer = new FormulaImporter();
        Pair<File, String> result = importer.showDialog();
        if (result == null) {
            JOptionPane.showMessageDialog(null, "No formula imported.", "Formula Import", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Formula in file `" + result.first().toString() + "' will be imported as `" + result.second() + "'.", "Formula Import", JOptionPane.INFORMATION_MESSAGE);
        }
        System.exit(0);
    }
}
