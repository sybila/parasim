package org.sybila.parasim.extension.projectmanager.view.experiment;

import org.sybila.parasim.extension.projectmanager.view.names.NameChooserModel;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface ExperimentSettingsModel {

    public NameChooserModel getFormulaChooser();

    public NameChooserModel getSimulationChooser();

    public NameChooserModel getRobustnessChooser();

    public void valuesChanged(ExperimentSettingsValues values);

    public void annotationChanged(String annotation);
}
