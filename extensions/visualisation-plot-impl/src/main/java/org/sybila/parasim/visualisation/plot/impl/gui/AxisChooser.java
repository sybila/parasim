/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.visualisation.plot.impl.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import org.sybila.parasim.model.ode.PointVariableMapping;

/**
 * Combo box used to select displayed axis. Comes in pairs, where each listener
 * must choose different axis.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class AxisChooser extends JComboBox {

    private DefaultComboBoxModel model;
    private PointVariableMapping names;
    private AxisChooser paired;
    private int omit;

    /**
     * @param names Axes names.
     * @param dimension How many of axes names should be displayed.
     * @param initial Initial selected axis.
     * @param leaveOut Initial omitted axis (i.e. selected by paired
     * AxisChooser).
     * @param updateAxes This action will be executed when a new axis is
     * selected.
     */
    private AxisChooser(int dimension, PointVariableMapping names, int initial, int leaveOut, ActionListener updateAxes) {
        if (dimension <= 0) {
            throw new IllegalArgumentException("Dimension not positive.");
        }
        if (initial < 0 || initial >= dimension) {
            throw new IllegalArgumentException("Initial selected item not in range.");
        }
        if (leaveOut < 0 || leaveOut >= dimension) {
            throw new IllegalArgumentException("Initial omitted item not in range.");
        }
        if (initial == leaveOut) {
            throw new IllegalArgumentException("Selected item cannot be omitted.");
        }

        model = new DefaultComboBoxModel();
        setModel(model);

        this.names = names;
        omit = leaveOut;
        for (int i = 0; i < dimension; i++) {
            if (i != leaveOut) {
                addItem(names.getName(new Integer(i)));
            }
        }
        if (leaveOut < initial) {
            initial--;
        }
        setSelectedIndex(initial);

        addActionListener(updateAxes);
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                paired.repair();
            }
        });
    }

    /**
     * Return selected axis.
     *
     * @return Dimension of selected axis.
     */
    public int getSelected() {
        int sel = getSelectedIndex();
        if (sel >= omit) {
            sel++;
        }
        return sel;
    }

    /**
     * Called by paired AxisChooser when it chooses an axis. Updates omitted
     * axis.
     */
    private void repair() {
        int selected = paired.getSelected();
        if (selected != omit) {
            model.insertElementAt(names.getName(new Integer(omit)), omit);
            model.removeElementAt(selected);
            omit = selected;
        }
    }

    /**
     * Create two paired AxisChoosers.
     *
     * @param names Axis names.
     * @param dimension Number of axes.
     * @param updateAxes Action to be performed when an axis is chosen.
     * @return Two paired AxisChoosers.
     */
    public static AxisChooser[] getPairedAxes(int dimension, PointVariableMapping names, ActionListener updateAxes) {
        AxisChooser[] axes = new AxisChooser[2];
        axes[0] = new AxisChooser(dimension, names, 0, 1, updateAxes);
        axes[1] = new AxisChooser(dimension, names, 1, 0, updateAxes);
        axes[0].paired = axes[1];
        axes[1].paired = axes[0];
        return axes;
    }
}
