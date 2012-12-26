package org.sybila.parasim.extension.exporter.api;

import java.io.File;
import java.io.IOException;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.verification.result.VerificationResult;

public interface ResultExporter {

    void export(VerificationResult result, OdeSystem system, File file, Object... args) throws IOException;

}
