package org.sybila.parasim.extension.projectManager.view.experiment;

import org.sybila.parasim.extension.projectManager.view.names.NameChooserModel;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface ExperimentSettingsModel {

    public NameChooserModel getFormulaChooser();

    public NameChooserModel getSimulationChooser();

    public NameChooserModel getRobustnessChooser();

    public void valuesChanged(ExperimentSettingsValues values);
}
