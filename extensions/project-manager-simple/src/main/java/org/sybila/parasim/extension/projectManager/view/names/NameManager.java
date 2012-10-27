package org.sybila.parasim.extension.projectManager.view.names;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
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
import org.sybila.parasim.util.SimpleLock;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class NameManager extends JPanel {

    private final SimpleLock lock = new SimpleLock();
    private ExtendedNameManagerModel model;
    private NameList chooser;
    private DefaultComboBoxModel<String> items;
    private Action newAction, renameAction, removeAction, saveAction, useAction;
    private JButton renameBtn;

    public NameManager(NameManagerModel managerModel, Set<String> names) {
        if (managerModel == null) {
            throw new IllegalArgumentException("Argument (model) is null.");
        }
        if (names == null) {
            throw new IllegalArgumentException("Argument (names) is null.");
        }
        model = new NameManagerModelAdapter(managerModel);
        chooser = new NameList.Adapter();
        init(names);
    }

    public NameManager(ExtendedNameManagerModel managerModel, NameList nameChooser, Set<String> names) {
        if (managerModel == null) {
            throw new IllegalArgumentException("Argument (model) is null.");
        }
        if (nameChooser == null) {
            throw new IllegalArgumentException("Argument (chooser) is null.");
        }
        if (names == null) {
            throw new IllegalArgumentException("Argument (names) is null.");
        }
        model = managerModel;
        chooser = nameChooser;
        init(names);
    }

    private void init(Set<String> newNames) {
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
                lock.lock();
                String name = JOptionPane.showInputDialog(NameManager.this, "Select new name for `" + getSelectedName() + "':", "Rename dialog", JOptionPane.QUESTION_MESSAGE);
                if (name != null) {
                    if (name.equals(getSelectedName())) {
                        JOptionPane.showMessageDialog(NameManager.this, "You have chosen the same name.", "Rename dialog", JOptionPane.INFORMATION_MESSAGE);
                    } else if (items.getIndexOf(name) != -1) {
                        JOptionPane.showMessageDialog(NameManager.this, "Name `" + name + "' already in use.", "Rename error", JOptionPane.ERROR_MESSAGE);
                    } else if (model.renameCurrent(getSelectedName(), name)) {
                        int pos = items.getIndexOf(items.getSelectedItem());
                        chooser.renameName(getSelectedName(), name);
                        items.removeElementAt(pos);
                        items.insertElementAt(name, pos);
                        items.setSelectedItem(name);
                    }
                }
                lock.unlock();
            }
        };
        removeAction = new AbstractAction("Remove") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                lock.lock();
                if (model.removeCurrent(getSelectedName())) {
                    chooser.removeName(getSelectedName());
                    Object current = items.getSelectedItem();
                    setNewName();
                    items.removeElement(current);
                }
                lock.unlock();
            }
        };
        saveAction = new AbstractAction("Save") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                saveData();
            }
        };
        useAction = new AbstractAction("Use") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (!isNewName() || saveData()) {
                    model.chooseCurrent(getSelectedName());
                    chooser.selectName(getSelectedName());
                }
            }
        };

        items = new DefaultComboBoxModel<>(newNames.toArray(new String[0]));
        JComboBox<String> choice = new JComboBox<>(items);
        choice.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (lock.isAccessible()) {
                    if (isNewName()) {
                        int result = JOptionPane.showConfirmDialog(NameManager.this, "If you continue with this operation, you will lose data. Do you want to save them?", "Unsaved data", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                        switch (result) {
                            case JOptionPane.NO_OPTION:
                                model.selectionChanged(getSelectedName());
                                setKnownName();
                                return;
                            case JOptionPane.YES_OPTION:
                                String name = getSelectedName();
                                if (saveData()) {
                                    lock.lock();
                                    items.setSelectedItem(name);
                                    model.selectionChanged(name);
                                    lock.unlock();
                                    return;
                                }
                            case JOptionPane.CANCEL_OPTION:
                            default:
                                lock.lock();
                                items.setSelectedItem(null);
                                lock.unlock();
                        }
                    } else {
                        model.selectionChanged(getSelectedName());
                    }
                }
            }
        });

        renameBtn = new JButton();
        setNewName();

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(choice);

        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.LINE_AXIS));
        buttons.add(new JButton(newAction));
        buttons.add(renameBtn);
        buttons.add(new JButton(removeAction));
        if (!(chooser instanceof NameList.Adapter)) {
            buttons.add(new JButton(useAction));
        }
        add(buttons);
    }

    private boolean saveData() {
        String name = JOptionPane.showInputDialog(NameManager.this, "Choose name:", "Save Dialog", JOptionPane.QUESTION_MESSAGE);
        if (name != null) {
            if (items.getIndexOf(name) != -1) {
                JOptionPane.showMessageDialog(NameManager.this, "Name `" + name + "' already in use.", "Save error", JOptionPane.ERROR_MESSAGE);
            } else if (model.saveCurrent(name)) {
                lock.lock();
                chooser.addName(name);
                items.addElement(name);
                items.setSelectedItem(name);
                setKnownName();
                lock.unlock();
                return true;
            }
        }
        return false;
    }

    private void setNewName() {
        lock.lock();
        items.setSelectedItem(null);
        newAction.setEnabled(false);
        removeAction.setEnabled(false);
        renameBtn.setAction(saveAction);
        lock.unlock();
    }

    private void setKnownName() {
        newAction.setEnabled(true);
        removeAction.setEnabled(true);
        renameBtn.setAction(renameAction);
    }

    private boolean isNewName() {
        return !newAction.isEnabled();
    }

    private String getSelectedName() {
        Object item = items.getSelectedItem();
        if (item == null) {
            return null;
        }
        return item.toString();
    }

    public void setSelectedName(String name) {
        lock.lock();
        items.setSelectedItem(name);
        lock.unlock();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFrame frame = new JFrame();
                frame.setSize(300, 150);
                NameManager manager = new NameManager(new ExtendedNameManagerModel() {

                    @Override
                    public void selectionChanged(String name) {
                        System.out.println("Selection changed to `" + name + "'.");
                    }

                    @Override
                    public void newName() {
                        System.out.println("New name created.");
                    }

                    @Override
                    public boolean renameCurrent(String name, String newName) {
                        System.out.println("`" + name + "' renamed to `" + newName + "'.");
                        return true;
                    }

                    @Override
                    public boolean saveCurrent(String name) {
                        System.out.println("This named saved as `" + name + "'.");
                        return true;
                    }

                    @Override
                    public boolean removeCurrent(String name) {
                        System.out.println("Name `" + name + "' has been removed.");
                        return true;
                    }

                    @Override
                    public void chooseCurrent(String name) {
                        System.out.println("Name `" + name + "' has been chosen.");
                    }
                }, new NameList() {

                    @Override
                    public void addName(String name) {
                        System.out.println("Name `" + name + "' added to the list.");
                    }

                    @Override
                    public void removeName(String name) {
                        System.out.println("Name `" + name + "' removed from the list.");
                    }

                    @Override
                    public void renameName(String name, String newName) {
                        System.out.println("Name `" + name + "' renamed to `" + newName + "' in the list.");
                    }

                    @Override
                    public void selectName(String name) {
                        System.out.println("Name `" + name + "' selected in the list.");
                    }
                }, Collections.<String>emptySet());


                frame.add(manager);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}
