/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.visualisation.plot.impl.gui;

import java.awt.BorderLayout;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class AxisSlider extends JPanel {

    private static final int INSET = 10;
    private JSlider slider;

    public AxisSlider(int dimension, String label, ChangeListener updateView) {
        setLayout(new BorderLayout(INSET, INSET));
        setBorder(new EmptyBorder(INSET, INSET, INSET, INSET));

        add(new JLabel(label), BorderLayout.PAGE_END);

        slider = new JSlider(JSlider.VERTICAL);
        slider.addChangeListener(updateView);
        add(slider, BorderLayout.CENTER);
    }

    public void setActive(boolean active) {
        setVisible(active);
        slider.setEnabled(active);
    }

    public boolean isActive() {
        return isVisible();
    }

    public int getValue() {
        return slider.getValue();
    }

    public void update(int ticks, int value) {
        slider.setModel(new DefaultBoundedRangeModel(value, 0, 0, ticks));
    }
}
