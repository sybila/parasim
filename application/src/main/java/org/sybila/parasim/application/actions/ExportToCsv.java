/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sybila.parasim.application.actions;

import java.io.File;
import org.sybila.parasim.application.ParasimOptions;
import org.sybila.parasim.application.model.Experiment;
import org.sybila.parasim.core.api.Manager;
import org.sybila.parasim.extension.exporter.api.ResultExporter;
import org.sybila.parasim.extension.exporter.impl.annotations.Csv;
import org.sybila.parasim.model.verification.result.VerificationResult;

/**
 *
 * @author jpapouse
 */
public class ExportToCsv extends AbstractAction<Void> {

    private final VerificationResult result;
    private final Experiment experiment;

    public ExportToCsv(VerificationResult result, Experiment experiment, Manager manager, ParasimOptions options) {
        super(manager, options);
        this.result = result;
        this.experiment = experiment;
    }

    @Override
    public boolean isEnabled() {
        return getOptions().isTerminal() && getOptions().getCsvFile() != null;
    }

    @Override
    public Void call() throws Exception {
        getManager().resolve(ResultExporter.class, Csv.class).export(result, experiment.getOdeSystem(), new File(getOptions().getCsvFile()));
        return null;
    }



}
