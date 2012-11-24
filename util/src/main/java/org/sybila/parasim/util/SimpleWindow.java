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
}
