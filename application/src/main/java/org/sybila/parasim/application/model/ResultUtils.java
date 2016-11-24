/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
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
import java.util.ArrayList;
import java.util.List;
import org.sybila.parasim.computation.verification.api.Monitor;
import org.sybila.parasim.model.verification.Robustness;

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
