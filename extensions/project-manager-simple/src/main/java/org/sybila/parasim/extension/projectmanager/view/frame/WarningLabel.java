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
package org.sybila.parasim.extension.projectmanager.view.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import javax.swing.JLabel;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class WarningLabel {

    private final JLabel label;

    public WarningLabel(Container parent) {
        label = new JLabel("This configuration is used in an experiment.");
        label.setIcon(IconSource.getInfoIcon());
        parent.add(label, BorderLayout.PAGE_START);
        label.setForeground(Color.GRAY);
        label.setVisible(false);
    }

    public void setVisible(boolean visibility) {
        label.setVisible(visibility);
    }
}
