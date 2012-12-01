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
package org.sybila.parasim.extension.projectmanager.view.names;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.sybila.parasim.util.SimpleLock;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class NameChooser extends JPanel implements NameList {

    private final SimpleLock lock = new SimpleLock();
    private NameChooserModel model;
    private Set<String> names;
    private DefaultComboBoxModel<String> choiceModel;
    private Action seeAction;

    public NameChooser(NameChooserModel chooserModel, Set<String> contents) {
        if (chooserModel == null) {
            throw new IllegalArgumentException("Argument (model) is null.");
        }
        if (contents == null) {
            throw new IllegalArgumentException("Argument (contents) is null.");
        }
        model = chooserModel;
        names = new HashSet<>(contents);

        choiceModel = new DefaultComboBoxModel<>(contents.toArray(new String[0]));
        seeAction = new AbstractAction(">>") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                model.seeName(getSelectedName());
            }
        };
        seeAction.setEnabled(false);

        setLayout(new BorderLayout());
        JComboBox<String> list = new JComboBox<>(choiceModel);
        list.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (lock.isAccessible()) {
                    seeAction.setEnabled(true);
                    model.chooseName(getSelectedName());
                }
            }
        });
        add(list, BorderLayout.CENTER);

        lock.lock();
        list.setSelectedItem(null);
        lock.unlock();

        add(new JButton(seeAction), BorderLayout.LINE_END);
    }

    private String getSelectedName() {
        Object selected = choiceModel.getSelectedItem();
        if (selected == null) {
            return null;
        }
        return selected.toString();
    }

    private void checkNotNull(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Argument (name) is null.");
        }
    }

    @Override
    public void addName(String name) {
        checkNotNull(name);
        if (!names.add(name)) {
            throw new IllegalArgumentException("Name `" + name + "' is already in list.");
        }
        lock.lock();
        choiceModel.addElement(name);
        if (choiceModel.getSize() == 1) {
            choiceModel.setSelectedItem(null);
        }
        lock.unlock();
    }

    @Override
    public void removeName(String name) {
        checkNotNull(name);
        if (!names.remove(name)) {
            throw new IllegalArgumentException("List does not contain the name `" + name + "'.");
        }
        lock.lock();
        if (name.equals(getSelectedName())) {
            choiceModel.setSelectedItem(null);
            seeAction.setEnabled(false);
        }
        choiceModel.removeElement(name);
        lock.unlock();
    }

    @Override
    public void renameName(String name, String newName) {
        checkNotNull(name);
        checkNotNull(newName);
        if (newName.equals(name)) {
            throw new IllegalArgumentException("Arguments name and newName are the same (" + name + "), no rename possible.");
        }
        if (names.contains(newName)) {
            throw new IllegalArgumentException("New name `" + newName + "' is already contained in the list.");
        }
        if (!names.remove(name)) {
            throw new IllegalArgumentException("There is no name `" + name + "' in the list.");
        }
        names.add(newName);
        lock.lock();
        boolean move = name.equals(getSelectedName());
        int pos = choiceModel.getIndexOf(name);
        choiceModel.removeElementAt(pos);
        choiceModel.insertElementAt(newName, pos);
        if (move) {
            choiceModel.setSelectedItem(newName);
        }
        lock.unlock();
    }

    @Override
    public void selectName(String name) {
        if (name != null && !names.contains(name)) {
            throw new IllegalArgumentException("There is no name `" + name + "' in the list.");
        }
        lock.lock();
        choiceModel.setSelectedItem(name);
        seeAction.setEnabled(name != null);
        lock.unlock();
    }

    @Override
    public Set<String> getNames() {
        return Collections.unmodifiableSet(names);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFrame frame = new JFrame();
                frame.setSize(250, 50);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                NameChooser chooser = new NameChooser(new NameChooserModel() {

                    @Override
                    public void chooseName(String name) {
                        System.out.println("Name `" + name + "' chosen.");
                    }

                    @Override
                    public void seeName(String name) {
                        System.out.println("Name `" + name + "' referenced.");
                    }
                }, Collections.<String>emptySet());
                frame.add(chooser);

                frame.setVisible(true);
                chooser.addName("A");
            }
        });
    }
}
