package org.sybila.parasim.extension.projectManager.view;

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
    private JFileChooser chooser;
    private JTextField nameField;

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

        chooser = new JFileChooser(System.getProperty("user.home"));
        chooser.setControlButtonsAreShown(false);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("XML files", "xml"));
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("STL formulae", "stl"));

        chooser.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                nameField.setText(removeExtension(chooser.getSelectedFile()));
            }
        });

        chooser.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Formula File"));
        importer.getContentPane().add(chooser);

        nameField = new JTextField();
        nameField.setBorder(null);
        nameField.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Formula Name"));
        importer.getContentPane().add(nameField);

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
