package org.sybila.parasim.extension.projectmanager.api;

import org.sybila.parasim.util.SimpleWindow;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface ProjectManager extends SimpleWindow {

    public void setExperimentListener(ExperimentListener target);

    public ExperimentListener getExperimentListener();
}
