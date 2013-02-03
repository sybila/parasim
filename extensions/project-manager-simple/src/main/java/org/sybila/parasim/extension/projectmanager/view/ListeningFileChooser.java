/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ListeningFileChooser extends JFileChooser {

    private final List<ActionListener> changeListeners = new ArrayList<>();

    public ListeningFileChooser() {
        super(".");
        //super(System.getProperty("user.home"));
        setControlButtonsAreShown(false);
        setMultiSelectionEnabled(false);
        addPropertyChangeListener(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY, new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                ActionEvent event = new ActionEvent(ListeningFileChooser.this, ActionEvent.ACTION_PERFORMED, JFileChooser.SELECTED_FILE_CHANGED_PROPERTY);
                for (ActionListener change : changeListeners) {
                    change.actionPerformed(event);
                }
            }
        });

        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "openDir");
        getActionMap().put("openDir", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (getSelectedFile() != null && getSelectedFile().isDirectory()) {
                    setCurrentDirectory(getSelectedFile());
                }
            }
        });
    }

    public void addSelectedFileChangedListener(ActionListener al) {
        changeListeners.add(al);
    }

    public void removeSelectedFileChangedListener(ActionListener al) {
        changeListeners.remove(al);
    }

    public ActionListener[] getSelectedFileChangedListeners() {
        return changeListeners.toArray(new ActionListener[0]);
    }
}
