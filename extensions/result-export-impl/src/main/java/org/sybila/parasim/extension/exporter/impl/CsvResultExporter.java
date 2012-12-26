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
