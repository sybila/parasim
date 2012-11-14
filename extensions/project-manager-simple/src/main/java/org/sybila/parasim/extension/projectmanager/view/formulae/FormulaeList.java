package org.sybila.parasim.extension.projectmanager.view.formulae;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
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
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.sybila.parasim.extension.projectmanager.view.names.NameList;

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
    private NameList chooser;
    //
    private JList<String> list;

    public FormulaeList(FormulaeListModel formulaeModel, NameList nameChooser) {
        if (formulaeModel == null) {
            throw new IllegalArgumentException("Argument (model) is null.");
        }
        if (nameChooser == null) {
            throw new IllegalArgumentException("Argument (chooser) is null.");
        }
        model = formulaeModel;
        chooser = nameChooser;

        names = new HashSet<>(nameChooser.getNames());
        listModel = new DefaultListModel<>();
        for (String name : names) {
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
                    chooser.addName(name);
                }
            }
        };
        removeAction = new AbstractAction("Remove") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                String name = list.getSelectedValue();
                if (model.remove(name)) {
                    if (!names.remove(name)) {
                        throw new IllegalStateException("Name `" + name + "' cannot be removed.");
                    }
                    listModel.removeElement(name);
                    chooser.removeName(name);
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
                        if (model.rename(name, newName)) {
                            names.remove(name);
                            names.add(newName);
                            chooser.renameName(name, newName);
                            listModel.set(list.getSelectedIndex(), newName);
                        }
                    }
                }
            }
        };
        chooseAction = new AbstractAction("Use") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                String name = list.getSelectedValue();
                if (model.choose(name)) {
                    chooser.selectName(name);
                }
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

        list.setBorder(new CompoundBorder(new LineBorder(Color.GRAY), new EmptyBorder(2, 2, 2, 2)));

        setLayout(new BorderLayout(3, 0));
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

    public void setSelectedName(String name) {
        if (names.contains(name)) {
            list.setSelectedValue(name, true);
        } else {
            throw new IllegalArgumentException("No formula of name `" + name + "'.");
        }
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
                    public boolean remove(String name) {
                        System.out.println("Formula `" + name + "' deleted.");
                        return true;
                    }

                    @Override
                    public boolean rename(String name, String newName) {
                        System.out.println("Formula `" + name + "' renamed to `" + newName + "'.");
                        return true;
                    }

                    @Override
                    public boolean choose(String name) {
                        System.out.println("Formula `" + name + "' chosen.");
                        return true;
                    }
                }, new NameList.Adapter());
                frame.add(list);
                frame.setVisible(true);
            }
        });
    }
}
