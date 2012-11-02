/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.application.model;

import dk.ange.octave.OctaveEngine;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.sybila.parasim.computation.verification.api.Monitor;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.model.verification.result.VerificationResult;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ResultUtils {

    private ResultUtils() {
    }

    public static void plot(Monitor monitor, OctaveEngine octave) {
        octave.eval(toOctaveString("time", "robustness", monitor));
        octave.eval("plot(time, robustness, ';" + monitor.getProperty().toString().replace("[", "{[").replace("]", "]}") + ";');drawnow();");
    }

    public static void plotRecursively(Monitor monitor, OctaveEngine octaveEngine) {
        List<Monitor> monitors = new ArrayList<>();
        monitors.add(monitor);
        for (int i=0; i<monitors.size(); i++) {
            monitors.addAll(monitors.get(i).getSubmonitors());
        }
        int sqrt = (int) Math.ceil(Math.sqrt(monitors.size()));
        int index = 1;
        for (Monitor m: monitors) {
            octaveEngine.eval("subplot(" + sqrt + ", " + sqrt + ", " + index + ");");
            plot(m, octaveEngine);
            index++;
        }

    }

    public static void toCSV(VerificationResult verificationResult, OdeSystem ode, File cvsFile) throws IOException {
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

    private static String toOctaveString(String xName, String yName, Monitor monitor) {
        boolean first = true;
        StringBuilder yBuilder = new StringBuilder().append(yName).append(" = [");
        StringBuilder xBuilder = new StringBuilder().append(xName).append(" = [");
        for (Robustness robustness : monitor) {
            if (first) {
                first = false;
            } else {
                yBuilder.append(", ");
                xBuilder.append(", ");
            }
            yBuilder.append(robustness.getValue());
            xBuilder.append(robustness.getTime());
        }
        yBuilder.append("];");
        xBuilder.append("];");
        return yBuilder.toString() + xBuilder.toString();
    }

}
