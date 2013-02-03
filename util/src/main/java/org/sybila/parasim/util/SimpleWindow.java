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
package org.sybila.parasim.util;

import java.awt.event.WindowListener;

/**
 * Container for application window which should not be modifiable.
 * Intentionally uses the same methods which are implemented by {@link java.awt.Window}
 * and by extension {@link javax.swing.JFrame}.
 *
 * @see java.awt.Window;
 * @see javax.swing.JFrame;
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface SimpleWindow {

    public void setVisible(boolean visibility);

    public boolean isVisible();

    public void addWindowListener(WindowListener listener);

    public void removeWindowListener(WindowListener listener);

    public WindowListener[] getWindowListeners();

    public void dispose();

    public boolean isDisplayable();
}
