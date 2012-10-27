package org.sybila.parasim.application.model;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.verification.result.VerificationResult;

/**
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ResultUtils {

    private ResultUtils() {
    }

    public static void toCVS(VerificationResult verificationResult, OdeSystem ode, File cvsFile) throws IOException {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(cvsFile);
            for (int dim=0; dim<ode.dimension(); dim++) {
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
            for (int i=0; i<verificationResult.size(); i++) {
                int dim = 0;
                for (Float f: verificationResult.getPoint(i)) {
                    if (dim > 0) {
                        writer.print("\t");
                    }
                    writer.print(f.toString());
                    dim++;
                }
                writer.print("\t" + verificationResult.getRobustness(i));
                writer.println();
            }
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

}
