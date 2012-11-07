package org.sybila.parasim.extension.projectmanager.api;

import java.awt.event.WindowListener;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface ProjectManager {

    public void setVisible(boolean visibility);

    public boolean isVisible();

    public void setExperimentListener(ExperimentListener target);

    public ExperimentListener getExperimentListener();

    public void addWindowListener(WindowListener listener);

    public void removeWindowListener(WindowListener listener);

    public WindowListener[] getWindowListeners();
}
