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
package org.sybila.parasim.extension.projectmanager.view.frame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.UIManager;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public final class IconSource {

    private IconSource() {
    }

    private static Action getAction(String iconSrc, String alternateText, final ActionListener listener) {
        Icon icon = UIManager.getIcon(iconSrc);
        if (icon != null) {
            return new AbstractAction("", icon) {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    listener.actionPerformed(ae);
                }
            };
        }
        return new AbstractAction(alternateText) {

            @Override
            public void actionPerformed(ActionEvent ae) {
                listener.actionPerformed(ae);
            }
        };
    }

    public static Icon getWarningIcon() {
        return UIManager.getIcon("OptionPane.warningIcon");
    }

    public static Icon getInfoIcon() {
        return UIManager.getIcon("OptionPane.informationIcon");
    }

    public static Action getNewAction(ActionListener listener) {
        return getAction("FileView.fileIcon", "New project", listener);
    }

    public static Action getLoadAction(ActionListener listener) {
        return getAction("FileView.directoryIcon", "Open project", listener);
    }

    public static Action getSaveAction(ActionListener listener) {
        return getAction("FileView.floppyDriveIcon", "Save project", listener);
    }
}
