/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sybila.parasim.application.actions;

import org.sybila.parasim.application.ParasimOptions;
import org.sybila.parasim.application.model.Experiment;
import org.sybila.parasim.application.model.ExperimentImpl;
import org.sybila.parasim.core.api.Manager;

/**
 *
 * @author jpapouse
 */
public class LoadExperiment extends AbstractAction<Experiment> {

    public LoadExperiment(Manager manager, ParasimOptions options) {
        super(manager, options);
    }

    @Override
    public boolean isEnabled() {
        return (getOptions().isResultOnly() || getOptions().isTerminal()) && !getOptions().isServer();
    }

    @Override
    public Experiment call() throws Exception {
        if (getOptions().getExperimentFile() == null) {
            System.err.println("Experiment file not specified.");
            ParasimOptions.printHelp(System.out);
            System.exit(1);
        }
        return ExperimentImpl.fromPropertiesFile(getOptions().getExperimentFile());
    }

}
