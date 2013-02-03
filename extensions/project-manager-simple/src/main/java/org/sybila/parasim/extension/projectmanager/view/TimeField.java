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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import org.sybila.parasim.util.Pair;
import org.sybila.parasim.util.SimpleLock;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class TimeField extends JPanel {

    private static final List<TimeUnit> allowedUnits = Arrays.asList(TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS, TimeUnit.DAYS);
    //
    private final List<ActionListener> listeners = new ArrayList<>();
    private final SimpleLock lock = new SimpleLock();
    //
    private final CommitFormattedTextField amount;
    private final JComboBox<TimeUnit> unit;
    private TimeUnit current = TimeUnit.MINUTES;

    private long convertCeil(TimeUnit source, TimeUnit dest, long duration) {
        long result = dest.convert(duration, source);
        if (source.convert(result, dest) != duration) {
            result++;
        }
        return result;
    }

    public TimeField() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        unit = new JComboBox<>(allowedUnits.toArray(new TimeUnit[0]));
        amount = new CommitFormattedTextField(new Long(0));
        unit.setSelectedItem(TimeUnit.MINUTES);
        amount.addCommitListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (lock.isAccessible()) {
                    if (getValues().first() < 0) {
                        lock.lock();
                        amount.setValue(0);
                        lock.unlock();
                    }
                    fireChanges();
                }
            }
        });
        unit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (lock.isAccessible()) {
                    TimeUnit chosen = getValues().second();
                    if (!current.equals(chosen)) {
                        lock.lock();
                        amount.setValue(convertCeil(current, chosen, getValues().first()));
                        lock.unlock();
                    }
                    current = chosen;
                    fireChanges();
                }
            }
        });
        add(amount);
        add(unit);
    }

    private void fireChanges() {
        if (lock.isAccessible()) {
            for (ActionListener listener : listeners) {
                listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "values changed"));
            }
        }
    }

    public Pair<Long, TimeUnit> getValues() {
        return new Pair<>(((Number) amount.getValue()).longValue(), TimeUnit.valueOf(unit.getSelectedItem().toString()));
    }

    public void setValues(Pair<Long, TimeUnit> values) {
        lock.lock();
        amount.setValue(values.first());
        if (allowedUnits.contains(values.second())) {
            unit.setSelectedItem(values.second());
        } else {
            unit.setSelectedItem(TimeUnit.MINUTES);
        }
        lock.unlock();
    }

    public void addActionListener(ActionListener listener) {
        listeners.add(listener);
    }

    public void removeActionListener(ActionListener listener) {
        listeners.remove(listener);
    }

    public ActionListener[] getActionListeners() {
        return listeners.toArray(new ActionListener[0]);
    }
}
