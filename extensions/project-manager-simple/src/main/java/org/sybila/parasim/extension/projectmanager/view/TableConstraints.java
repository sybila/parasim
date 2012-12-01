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
package org.sybila.parasim.extension.projectmanager.view;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public final class TableConstraints {

    private TableConstraints() {
    }

    private static GridBagConstraints getDefaultConstraints() {
        GridBagConstraints result = new GridBagConstraints();
        //result.weightx = 1;
        //result.weighty = 1;
        result.fill = GridBagConstraints.BOTH;
        result.insets = new Insets(2, 1, 2, 1);
        return result;
    }

    public static GridBagConstraints getHeaderConstraints(int x) {
        GridBagConstraints result = getDefaultConstraints();
        result.gridx = x;
        result.gridy = 0;
        return result;
    }

    public static GridBagConstraints getRowConstraints(int y) {
        GridBagConstraints result = getDefaultConstraints();
        result.gridx = 0;
        result.gridy = y;
        return result;
    }

    public static GridBagConstraints getCellConstraints(int x, int y) {
        GridBagConstraints result = getDefaultConstraints();
        result.gridx = x;
        result.gridy = y;
        return result;
    }

    public static JLabel getHeaderLabel(String name) {
        JLabel result = new JLabel(name);
        result.setHorizontalAlignment(JLabel.CENTER);
        return result;
    }

    public static JLabel getRowLabel(String name) {
        JLabel result = new JLabel(name);
        result.setHorizontalAlignment(JLabel.RIGHT);
        result.setBackground(Color.WHITE);
        result.setOpaque(true);
        return result;
    }

    public static JLabel getHeaderLabelWithToolTip(String name, String toolTip) {
        JLabel result = getHeaderLabel(name);
        result.setToolTipText(toolTip);
        return result;
    }

    public static JLabel getCellLabel(String text) {
        JLabel result = new JLabel(text);
        result.setBackground(Color.WHITE);
        result.setOpaque(true);
        result.setHorizontalAlignment(JLabel.CENTER);
        return result;
    }
}
