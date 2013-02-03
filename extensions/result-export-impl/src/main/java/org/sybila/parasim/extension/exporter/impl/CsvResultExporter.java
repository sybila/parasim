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
package org.sybila.parasim.extension.exporter.impl;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import org.sybila.parasim.extension.exporter.api.ResultExporter;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.verification.result.VerificationResult;

public class CsvResultExporter implements ResultExporter {

    @Override
    public void export(VerificationResult result, OdeSystem ode, File output, Object... args) throws IOException {
        try (PrintWriter writer = new PrintWriter(output)) {
            for (int dim = 0; dim < ode.dimension(); dim++) {
                if (dim > 0) {
                    writer.print("\t");
                }
                if (ode.isVariable(dim)) {
                    writer.print(ode.getVariable(dim).getName());
                }
                if (ode.isParamater(dim)) {
                    writer.print(ode.getParameter(dim).getName());
                }
            }
            writer.print("\tRobustness");
            writer.println();
            for (int i = 0; i < result.size(); i++) {
                int dim = 0;
                for (Float f : result.getPoint(i)) {
                    if (dim > 0) {
                        writer.print("\t");
                    }
                    writer.print(f.toString());
                    dim++;
                }
                writer.print("\t" + result.getRobustness(i));
                writer.println();
            }
        }
    }
}
