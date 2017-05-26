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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.text.DecimalFormat;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import org.sybila.parasim.model.ode.PointVariableMapping;
import org.sybila.parasim.visualisation.plot.impl.ResultPlotterConfiguration;

/**
 * Status bar displaying current coordinate in model. This is discerned from active
 * {@link AxisSlider}s and {@link CanvasPane}.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class StatusBar extends JPanel {

    private static final int PADDING = 2;
    private static final Border border = new CompoundBorder(new BevelBorder(BevelBorder.LOWERED),
            new EmptyBorder(PADDING, PADDING, PADDING, PADDING));

    /**
     * Label displaying one coordinate.
     */
    private class StatusLabel extends JPanel {

        private static final int INSET = 5;
        private JLabel caption;

        public StatusLabel(String name) {
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            setBorder(border);

            JLabel cptn = new JLabel(name + ":");
            cptn.setFont(labelFont);
            add(cptn);
            add(Box.createRigidArea(new Dimension(INSET, INSET)));

            caption = new JLabel();
            caption.setFont(labelFont);
            add(caption);
        }

        public void setCaption(float value) {
            caption.setText(format.format(value));
        }
    }
    private StatusLabel[] labels;
    private float[] values;
    private Font labelFont;
    private DecimalFormat format;

    /**
     * Initializes status bar. Uses the following configurable values:
     * <ul>
     *  <li>{@link ResultPlotterConfiguration#getStatusFontSize()} to determine labels size.</li>
     *  <li>{@link ResultPlotterConfiguration#getStatusDecimalDigits()} to determine number of displayed decimal digits.</li>
     * </ul>
     *
     * @param conf This extension configuration.
     * @param dimension Number of result dimensions.
     * @param names Names of model variables.
     */
    public StatusBar(ResultPlotterConfiguration conf, int dimension, PointVariableMapping names) {
        setLayout(new BorderLayout());
        labelFont = new Font("SansSerif", Font.PLAIN, conf.getStatusFontSize());
        StringBuilder formatBuilder = new StringBuilder("0.");
        for (int i = 0; i < conf.getStatusDecimalDigits(); i++) {
            formatBuilder.append('#');
        }
        format = new DecimalFormat(formatBuilder.toString());

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.LINE_AXIS));
        labels = new StatusLabel[dimension];
        for (int i = 0; i < dimension; i++) {
            labels[i] = new StatusLabel(names.getName(i));
            labelPanel.add(labels[i]);
        }
        add(labelPanel, BorderLayout.LINE_START);
        values  = new float[dimension];
        JPanel filler = new JPanel();
        filler.setBorder(border);
        add(filler, BorderLayout.CENTER);
    }

    public float getValue(int index) {
        return values[index];
    }

    /**
     * Set value displayed at given coordinate.
     * @param index Coordinate.
     * @float value Displayed value.
     */
    public void setValue(int index, float value) {
        values[index] = value;
        labels[index].setCaption(value);
    }
}
