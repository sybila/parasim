package org.sybila.parasim.extension.projectManager.api;

import org.sybila.parasim.application.model.Experiment;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface ExperimentListener {

    public void performExperiment(Experiment target);

    public void showResult(Experiment target);
}
