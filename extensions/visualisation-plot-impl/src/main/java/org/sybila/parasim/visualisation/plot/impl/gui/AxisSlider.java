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
package org.sybila.parasim.visualisation.plot.impl.gui;

import java.awt.BorderLayout;
import java.util.Hashtable;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;

/**
 * Slider used to choose discrete position on unprojected axes.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 * @see LayerFactory
 */
public class AxisSlider extends JPanel {

    private static final int INSET = 10;
    private JSlider slider;
    private float min, max;

    /**
     * Create new slider with given labels and action called on update.
     * @param label Name of the axis.
     * @param updateView Action done when slider value is changed.
     * @param min Minimum label.
     * @param max Maximum label.
     */
    public AxisSlider(String label, ChangeListener updateView, float min, float max) {
        this.min = min;
        this.max = max;

        setLayout(new BorderLayout(INSET, INSET));
        setBorder(new EmptyBorder(INSET, INSET, INSET, INSET));

        add(new JLabel(label), BorderLayout.PAGE_END);

        slider = new JSlider(JSlider.VERTICAL);
        slider.addChangeListener(updateView);
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);
        slider.setMinorTickSpacing(1);
        slider.setSnapToTicks(true);
        add(slider, BorderLayout.CENTER);
    }

    /**
     * Hides/show slider.
     * @param active <code>false</code> when the slider should be disabled
     * (when its axis is projected), <code>true</code> when it should be enabled.
     */
    public void setActive(boolean active) {
        setVisible(active);
        slider.setEnabled(active);
    }

    /**
     *
     * @return
     */
    public boolean isActive() {
        return isVisible();
    }

    /**
     * Returns discrete position on axis.
     * @return Position in discrete steps (ticks) from the minimum.
     */
    public int getValue() {
        return slider.getValue();
    }

    /**
     * Updates the number of discrete positions on the axis
     * and the current position.
     * @param ticks Number of discrete positions.
     * @param value Current position.
     */
    public void update(int ticks, int value) {
        slider.setModel(new DefaultBoundedRangeModel(value, 0, 0, ticks-1));
        //note: fourth argument is max-value, so it is number of ticks - 1

        slider.setMajorTickSpacing(ticks);

        //create labels//
        Hashtable<Integer,JLabel> labels = new Hashtable<Integer, JLabel>();
        labels.put(0, new JLabel(Float.toString(min)));
        labels.put(ticks-1, new JLabel(Float.toString(max)));
        slider.setLabelTable(labels);
    }
}
