/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sybila.parasim.application.actions;

import org.sybila.parasim.application.ParasimOptions;
import org.sybila.parasim.application.model.Experiment;
import org.sybila.parasim.application.model.ExperimentLauncher;
import org.sybila.parasim.core.api.Manager;
import org.sybila.parasim.model.verification.result.VerificationResult;

/**
 *
 * @author jpapouse
 */
public class ExecuteComputation extends AbstractAction<VerificationResult> {

    private final Experiment experiment;

    public ExecuteComputation(Experiment experiment, Manager manager, ParasimOptions options) {
        super(manager, options);
        this.experiment = experiment;
    }

    @Override
    public boolean isEnabled() {
        return getOptions().isTerminal() && !getOptions().isServer();
    }

    @Override
    public VerificationResult call() throws Exception {
        return ExperimentLauncher.launch(getManager(), experiment);
    }

}
