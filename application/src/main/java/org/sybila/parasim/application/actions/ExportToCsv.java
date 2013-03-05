/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.application.actions;

import java.io.File;
import org.sybila.parasim.application.ParasimOptions;
import org.sybila.parasim.extension.projectmanager.api.Experiment;
import org.sybila.parasim.core.api.Manager;
import org.sybila.parasim.extension.exporter.api.ResultExporter;
import org.sybila.parasim.extension.exporter.impl.annotations.Csv;
import org.sybila.parasim.model.verification.result.VerificationResult;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
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
