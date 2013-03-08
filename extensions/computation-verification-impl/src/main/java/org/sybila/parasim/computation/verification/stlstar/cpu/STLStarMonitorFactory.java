package org.sybila.parasim.computation.verification.stlstar.cpu;

import org.sybila.parasim.computation.verification.api.Monitor;
import org.sybila.parasim.computation.verification.api.MonitorFactory;
import org.sybila.parasim.computation.verification.cpu.StarMonitor;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.verification.stl.Formula;

/**
 * Transforms a STL formula into monitor which monitors its "starred" version
 * (i.e. takes frozen-time values into account). Call the {@link #createMonitor}
 * method only on top-level formula. Otherwise, use {@link #createStarMonitor}.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public enum STLStarMonitorFactory implements MonitorFactory<Formula> {

    /**
     * Singleton instance.
     */
    INSTANCE;

    @Override
    public Monitor createMonitor(Trajectory trajectory, Formula property) {
        return createStarMonitor(trajectory, property);
    }

    /**
     * Creates a monitor which monitors formula over given trajectory for STS
     * semantics with frozen-time values.
     *
     * @param trajectory Trajectory to monitor over.
     * @param property Property to monitor.
     * @return A monitor using frozen-time semantics.
     */
    public StarMonitor createStarMonitor(Trajectory trajectory, Formula property) {
        switch (property.getType()) {
            default:
                throw new UnsupportedOperationException("There is no available monitor for formula type [" + property.getType() + "].");
        }
    }

    /**
     * Return an instance of this factory.
     *
     * @return A monitor factory.
     */
    public static MonitorFactory<Formula> getInstance() {
        return INSTANCE;
    }
}
