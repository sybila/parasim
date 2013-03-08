package org.sybila.parasim.computation.verification.cpu;

import org.sybila.parasim.computation.verification.api.Monitor;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.util.Coordinate;

/**
 * Monitor which uses the frozen-time value semantics of STL.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface StarMonitor extends Monitor {

    /**
     * Compute robustness for given point in time and frozen time space.
     *
     * @param index Indices of time and frozen times.
     * @return Robustness in given time and frozen time.
     */
    Robustness getRobustness(Coordinate index);
}
