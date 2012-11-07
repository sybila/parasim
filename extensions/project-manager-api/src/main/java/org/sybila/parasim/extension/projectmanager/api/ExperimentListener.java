package org.sybila.parasim.extension.projectmanager.api;

import org.sybila.parasim.application.model.LoadedExperiment;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface ExperimentListener {

    public void performExperiment(LoadedExperiment target);

    public void showResult(LoadedExperiment target);
}
