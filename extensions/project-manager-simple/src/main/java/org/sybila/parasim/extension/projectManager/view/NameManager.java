package org.sybila.parasim.extension.projectManager.view;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.BufferOverflowException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class NameManager extends JPanel {

    private int locked = 0;
    private NameManagerModel model;
    private JComboBox choice;
    private DefaultComboBoxModel items;
    private JButton renameBtn;
    private Action newAction, renameAction, removeAction, saveAction;

    private void lockSelection() {
        if (locked < Integer.MAX_VALUE) {
            locked++;
        } else {
            throw new BufferOverflowException();
        }
    }

    private void unlockSelection() {
        if (locked > 0) {
            locked--;
        }
    }

    private boolean isLocked() {
        return (locked > 0);
    }

    public NameManager(NameManagerModel managerModel) {
        if (managerModel == null) {
            throw new IllegalArgumentException("Argument (name manager model) is null.");
        }
        model = managerModel;
        newAction = new AbstractAction("New") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                model.newName();
                setNewName();
            }
        };
        renameAction = new AbstractAction("Rename") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                lockSelection();
                String name = JOptionPane.showInputDialog(NameManager.this, "Select new name for `" + getSelectedName() + "':", "Rename Dialog", JOptionPane.QUESTION_MESSAGE);
                if (name != null) {
                    if (name.equals(getSelectedName())) {
                        JOptionPane.showMessageDialog(NameManager.this, "You have chosen the same name.", "Rename Dialog", JOptionPane.INFORMATION_MESSAGE);
                    } else if (items.getIndexOf(name) != -1) {
                        JOptionPane.showMessageDialog(NameManager.this, "Object of the name `" + name + "' already exists.", "Rename Error", JOptionPane.ERROR_MESSAGE);
                    } else if (model.renameCurrent(name)) {
                        items.removeElement(getSelectedName());
                        items.addElement(name);
                        items.setSelectedItem(name);
                    }
                }
                unlockSelection();
            }
        };
        removeAction = new AbstractAction("Remove") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                lockSelection();
                if (model.removeCurrent()) {
                    Object current = items.getSelectedItem();
                    setNewName();
                    items.removeElement(current);
                }
                unlockSelection();
            }
        };
        saveAction = new AbstractAction("Save") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                lockSelection();
                String name = JOptionPane.showInputDialog(NameManager.this, "Select name for object:", "Save Dialog", JOptionPane.QUESTION_MESSAGE);
                if (name != null) {
                    if (items.getIndexOf(name) != -1) {
                        JOptionPane.showMessageDialog(NameManager.this, "Object of the name `" + name + "' already exists.", "Save Error", JOptionPane.ERROR_MESSAGE);
                    } else if (model.saveCurrent(name)) {
                        items.addElement(name);
                        items.setSelectedItem(name);
                        setKnownName();
                    }
                }
                unlockSelection();
            }
        };
        items = new DefaultComboBoxModel();
        choice = new JComboBox(items);
        choice.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (!isLocked()) {
                    model.selectionChanged(getSelectedName());
                    setKnownName();
                }
            }
        });
        renameBtn = new JButton(saveAction);
        removeAction.setEnabled(false);
        newAction.setEnabled(false);

        createControls();
    }

    private void createControls() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(choice);

        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.LINE_AXIS));
        buttons.add(new JButton(newAction));
        buttons.add(renameBtn);
        buttons.add(new JButton(removeAction));
        add(buttons);
    }

    private void setNewName() {
        lockSelection();
        items.setSelectedItem(null);
        newAction.setEnabled(false);
        renameBtn.setAction(saveAction);
        removeAction.setEnabled(false);
        unlockSelection();
    }

    private void setKnownName() {
        newAction.setEnabled(true);
        renameBtn.setAction(renameAction);
        removeAction.setEnabled(true);
    }

    public void populate(Set<String> items) {
        lockSelection();
        this.items = new DefaultComboBoxModel(items.toArray(new String[0]));
        choice.setModel(this.items);
        model.newName();
        setNewName();
        unlockSelection();
    }

    public void populate(String[] items) {
        populate(new HashSet<>(Arrays.asList(items)));
    }

    public void setSelectedName(String name) {
        items.setSelectedItem(name);
    }

    public String getSelectedName() {
        Object item = items.getSelectedItem();
        if (item == null) {
            return null;
        }
        return item.toString();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFrame testFrame = new JFrame();
                testFrame.setSize(500, 250);
                testFrame.getContentPane().setLayout(new BorderLayout());
                NameManager manager = new NameManager(new NameManagerModel() {

                    @Override
                    public void selectionChanged(String name) {
                        System.out.println("Selection changed to " + name + ".");
                    }

                    @Override
                    public void newName() {
                        System.out.println("New name introduced.");
                    }

                    @Override
                    public boolean renameCurrent(String name) {
                        System.out.println("Current name changed to " + name + ".");
                        return true;
                    }

                    @Override
                    public boolean saveCurrent(String name) {
                        System.out.println("Current object saved as " + name + ".");
                        return true;
                    }

                    @Override
                    public boolean removeCurrent() {
                        System.out.println("Current name removed.");
                        return true;
                    }
                });
                manager.populate(new String[]{"A", "B"});
                testFrame.getContentPane().add(manager, BorderLayout.PAGE_END);
                testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                testFrame.setVisible(true);
            }
        });
    }
}