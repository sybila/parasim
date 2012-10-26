package org.sybila.parasim.extension.projectManager.view.formulae;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class FormulaeList extends JPanel {

    private GridBagConstraints getButtonConstraints(int y) {
        GridBagConstraints result = new GridBagConstraints();
        result.gridx = 0;
        result.gridy = y;
        result.fill = GridBagConstraints.HORIZONTAL;
        return result;
    }
    //
    private FormulaeListModel model;
    private Set<String> names;
    private DefaultListModel<String> listModel;
    private Action addAction, removeAction, renameAction, chooseAction;
    //
    private JList<String> list;

    public FormulaeList(FormulaeListModel formulaeModel, Set<String> formulaeNames) {
        if (formulaeModel == null) {
            throw new IllegalArgumentException("Argument (model) is null.");
        }
        model = formulaeModel;

        names = new HashSet<>(formulaeNames);
        listModel = new DefaultListModel<>();
        for (String name : formulaeNames) {
            listModel.addElement(name);
        }

        addAction = new AbstractAction("Import") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                String name = model.add();
                if (name != null) {
                    if (!names.add(name)) {
                        throw new IllegalStateException("This name cannot be added to the list.");
                    }
                    listModel.addElement(name);
                }
            }
        };
        removeAction = new AbstractAction("Remove") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                String name = list.getSelectedValue();
                if (names.remove(name)) {
                    listModel.removeElement(name);
                    model.remove(name);
                    setItemSelected(false);
                }
            }
        };
        renameAction = new AbstractAction("Rename") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                String name = list.getSelectedValue();
                String newName = JOptionPane.showInputDialog(FormulaeList.this, "Select new name for formula `" + name + "':", "Rename dialog", JOptionPane.QUESTION_MESSAGE);
                if (newName != null) {
                    if (names.contains(newName)) {
                        JOptionPane.showMessageDialog(FormulaeList.this, "Formula of name `" + newName + "' already exists.", "Rename error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        if (!model.rename(name, newName)) {
                            JOptionPane.showMessageDialog(FormulaeList.this, "Unable to rename formula `" + name + "' to `" + newName + "'.", "Rename error", JOptionPane.ERROR_MESSAGE);
                        } else {
                            names.remove(name);
                            names.add(newName);
                            listModel.set(list.getSelectedIndex(), newName);
                        }
                    }
                }
            }
        };
        chooseAction = new AbstractAction("Use") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                model.choose(list.getSelectedValue());
            }
        };
        setItemSelected(false);
        list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent lse) {
                setItemSelected(true);
            }
        });

        setLayout(new BorderLayout());
        add(list, BorderLayout.CENTER);
        JPanel buttons = new JPanel(new GridBagLayout());
        buttons.add(new JButton(addAction), getButtonConstraints(0));
        buttons.add(new JButton(removeAction), getButtonConstraints(1));
        buttons.add(new JButton(renameAction), getButtonConstraints(2));
        buttons.add(new JButton(chooseAction), getButtonConstraints(3));
        add(buttons, BorderLayout.LINE_END);
    }

    private void setItemSelected(boolean isSelected) {
        removeAction.setEnabled(isSelected);
        renameAction.setEnabled(isSelected);
        chooseAction.setEnabled(isSelected);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                final JFrame frame = new JFrame();
                frame.setSize(300, 450);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                FormulaeList list = new FormulaeList(new FormulaeListModel() {

                    @Override
                    public String add() {
                        return JOptionPane.showInputDialog(frame, "Select a new formula:");
                    }

                    @Override
                    public void remove(String name) {
                        System.out.println("Formula `" + name + "' deleted.");
                    }

                    @Override
                    public boolean rename(String name, String newName) {
                        System.out.println("Formula `" + name + "' renamed to `" + newName + "'.");
                        return true;
                    }

                    @Override
                    public void choose(String name) {
                        System.out.println("Formula `" + name + "' chosen.");
                    }
                }, Collections.<String>emptySet());
                frame.add(list);
                frame.setVisible(true);
            }
        });
    }
}
