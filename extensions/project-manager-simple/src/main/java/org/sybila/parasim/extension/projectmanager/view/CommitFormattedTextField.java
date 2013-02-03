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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFormattedTextField;
import org.sybila.parasim.util.SimpleLock;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class CommitFormattedTextField extends JFormattedTextField {

    private List<ActionListener> commitListeners = new ArrayList<>();
    private SimpleLock lock = new SimpleLock();

    private void init() {
        addPropertyChangeListener("value", new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                if (lock.isAccessible()) {
                    for (ActionListener listener : commitListeners) {
                        listener.actionPerformed(new ActionEvent(CommitFormattedTextField.this, ActionEvent.ACTION_PERFORMED, "commit"));
                    }
                }
            }
        });
    }

    public CommitFormattedTextField() {
        init();
    }

    public CommitFormattedTextField(AbstractFormatterFactory factory) {
        super(factory);
        init();
    }

    public CommitFormattedTextField(AbstractFormatter formatter) {
        super(formatter);
        init();
    }

    public CommitFormattedTextField(Format format) {
        super(format);
        init();
    }

    public CommitFormattedTextField(Object value) {
        super(value);
        init();
    }

    public CommitFormattedTextField(AbstractFormatterFactory factory, Object currentValue) {
        super(factory, currentValue);
        init();
    }

    public void addCommitListener(ActionListener al) {
        commitListeners.add(al);
    }

    public void removeCommitListener(ActionListener al) {
        commitListeners.remove(al);
    }

    public ActionListener[] getCommitListeners() {
        return commitListeners.toArray(new ActionListener[0]);
    }

    @Override
    public void setValue(Object value) {
        if (lock != null) {
            lock.lock();
        }
        super.setValue(value);
        if (lock != null) {
            lock.unlock();
        }
    }
}
